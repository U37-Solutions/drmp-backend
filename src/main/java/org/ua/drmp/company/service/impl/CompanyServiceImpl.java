package org.ua.drmp.company.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.dto.CompanyDto;
import org.ua.drmp.company.dto.CompanyMapper;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.CompanyStatus;
import org.ua.drmp.company.entity.CompanyType;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.company.repo.CompanyTypeRepository;
import org.ua.drmp.company.service.CompanyService;
import org.ua.drmp.entity.User;
import org.ua.drmp.exception.ResourceNotFoundException;
import org.ua.drmp.exception.UserNotFoundException;
import org.ua.drmp.repo.TokenRepository;
import org.ua.drmp.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
	private final CompanyRepository companyRepository;
	private final CompanyMapper companyMapper;
	private final CompanyTypeRepository companyTypeRepository;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;

	@Override
	public List<CompanyDto> fetchAllCompanies() {
		List<Company> companies = companyRepository.findAll();
		return companies.stream().map(companyMapper::toDto).toList();
	}

	@Override
	public List<CompanyDto> fetchCompanyByStatus(Optional<CompanyStatus> status) {
		List<Company> companies = companyRepository.findCompaniesByStatus(status);
		return companies.stream().map(companyMapper::toDto).toList();
	}

	@Override
	public CompanyDto getCompany(Long companyId) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		return companyMapper.toDto(company);
	}

	@Override
	public CompanyDto updateCompany(Long companyId, CompanyDto dto) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		CompanyType type = companyTypeRepository.findById(dto.getCompanyTypeId())
			.orElseThrow(() -> new ResourceNotFoundException("CompanyType not found"));

		company.setName(dto.getName());
		company.setCode(dto.getCode());
		company.setContactName(dto.getContactName());
		company.setOwnershipType(company.getOwnershipType());
		company.setCompanyType(company.getCompanyType());
		company.setPhone(dto.getPhone());
		company.setEmail(dto.getEmail());
		company.setCompanyType(type);

		company.getSocials().clear();
		dto.getSocials().forEach(s -> company.getSocials()
			.add(companyMapper.toSocialEntity(s, company)));

		return companyMapper.toDto(companyRepository.save(company));
	}

	@Override
	@Transactional
	public void deleteCompany(Long companyId) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		Set<User> linkedUsers = company.getUsers();

		for (User user : linkedUsers) {
			user.getCompanies().remove(company);
			tokenRepository.deleteAll(tokenRepository.findAllValidTokensByUser(user.getId()));
			userRepository.delete(user);
		}

		company.getUsers().clear(); // на всяк випадок, щоб Hibernate не намагався оновлювати зв’язки
		companyRepository.delete(company);
	}


	@Override
	public void createCompany(CompanyDto companyDto) {
		User user = getUser();
		CompanyType type = companyTypeRepository.findById(companyDto.getCompanyTypeId())
			.orElseThrow(() -> new ResourceNotFoundException("CompanyType not found"));
		Company company = new Company();
		company.setName(companyDto.getName());
		company.setCode(companyDto.getCode());
		company.setContactName(companyDto.getContactName());
		company.setOwnershipType(company.getOwnershipType());
		company.setCompanyType(company.getCompanyType());
		company.setPhone(companyDto.getPhone());
		company.setEmail(companyDto.getEmail());
		company.setStatus(CompanyStatus.valueOf(companyDto.getStatus()));
		company.setCompanyType(type);
		company.setUsers(Set.of(user));
		company.getSocials().clear();
		companyDto.getSocials().forEach(s -> company.getSocials()
			.add(companyMapper.toSocialEntity(s, company)));

		companyMapper.toDto(companyRepository.save(company));
	}

	private User getUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

}
