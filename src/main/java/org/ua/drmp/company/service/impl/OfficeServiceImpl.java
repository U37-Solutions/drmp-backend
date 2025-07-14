package org.ua.drmp.company.service.impl;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.dto.OfficeDto;
import org.ua.drmp.company.dto.OfficeMapper;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.company.entity.OfficeRegion;
import org.ua.drmp.company.entity.ServiceOffice;
import org.ua.drmp.company.repo.CategoryRepository;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.company.repo.ConditionRepository;
import org.ua.drmp.company.repo.OfficeRegionRepository;
import org.ua.drmp.company.repo.OfficeRepository;
import org.ua.drmp.company.repo.ServiceRepository;
import org.ua.drmp.company.service.OfficeService;
import org.ua.drmp.entity.User;
import org.ua.drmp.exception.ForbiddenOperationException;
import org.ua.drmp.exception.ResourceNotFoundException;
import org.ua.drmp.exception.UserNotFoundException;
import org.ua.drmp.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {

	private final OfficeRepository officeRepository;
	private final CompanyRepository companyRepository;
	private final OfficeMapper officeMapper;
	private final OfficeRegionRepository regionRepository;
	private final ServiceRepository serviceRepository;
	private final CategoryRepository categoryRepository;
	private final ConditionRepository conditionRepository;
	private final UserRepository userRepository;

	@Override
	public OfficeDto getOffice(Long officeId) {
		return officeMapper.toDto(
			officeRepository.findById(officeId)
				.orElseThrow(() -> new ResourceNotFoundException("Office not found"))
		);
	}

	@Override
	public OfficeDto createOffice(Long companyId, OfficeDto dto) {
		User user = getUser();
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		if (!isOwnerOrAdmin(company, user)) {
			throw new ForbiddenOperationException("You can't add office to this company");
		}

		OfficeRegion region = regionRepository.findById(dto.getRegionId())
			.orElseThrow(() -> new ResourceNotFoundException("Region not found"));

		Set<ServiceOffice> services = new HashSet<>(serviceRepository.findAllById(dto.getServiceIds()));
		Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()));
		Set<Condition> conditions = new HashSet<>(conditionRepository.findAllById(dto.getConditionIds()));

		Office office = officeMapper.toEntity(dto, company, region, services, categories, conditions, user);
		return officeMapper.toDto(officeRepository.save(office));
	}

	@Override
	public OfficeDto updateOffice(Long officeId, OfficeDto dto) {
		Office office = officeRepository.findById(officeId)
			.orElseThrow(() -> new ResourceNotFoundException("Office not found"));

		if (!isUserLinkedToCompany(office.getCompany(), getUser())) {
			throw new ForbiddenOperationException("Not allowed to update this office");
		}

		OfficeRegion region = regionRepository.findById(dto.getRegionId())
			.orElseThrow(() -> new ResourceNotFoundException("Region not found"));

		Set<ServiceOffice> services = new HashSet<>(serviceRepository.findAllById(dto.getServiceIds()));
		Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()));
		Set<Condition> conditions = new HashSet<>(conditionRepository.findAllById(dto.getConditionIds()));

		office.setWorkSchedule(dto.getWorkSchedule());
		office.setDonorSupport(dto.getDonorSupport());
		office.setAdditionalDescription(dto.getAdditionalDescription());
		office.setLatitude(dto.getLatitude());
		office.setLongitude(dto.getLongitude());
		office.setLocationName(dto.getLocationName());
		office.setRegion(region);
		office.setServices(services);
		office.setCategories(categories);
		office.setConditions(conditions);

		return officeMapper.toDto(officeRepository.save(office));
	}

	@Override
	public void deleteOffice(Long officeId) {
		if (!getUser().hasRole("ADMIN")) {
			throw new ForbiddenOperationException("Only admin can delete offices");
		}
		officeRepository.deleteById(officeId);
	}

	private boolean isOwnerOrAdmin(Company company, User user) {
		return company.getUser().getId().equals(user.getId()) || user.hasRole("ADMIN");
	}

	private boolean isUserLinkedToCompany(Company company, User user) {
		return isOwnerOrAdmin(company, user);
	}

	private User getUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
	}
}
