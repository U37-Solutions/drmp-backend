package org.ua.drmp.company.service.impl;

import java.util.HashSet;
import java.util.List;
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
import org.ua.drmp.company.entity.ServiceOffice;
import org.ua.drmp.company.repo.CategoryRepository;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.company.repo.ConditionRepository;
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
	private final ServiceRepository serviceRepository;
	private final CategoryRepository categoryRepository;
	private final ConditionRepository conditionRepository;
	private final UserRepository userRepository;

	@Override
	public List<OfficeDto> fetchAllOfficeByCompanyId(Long companyId) {
		return officeRepository.findAllByCompanyId(companyId).stream()
			.map(officeMapper::toDto)
			.toList();
	}

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

		Set<ServiceOffice> services = new HashSet<>(serviceRepository.findAllById(dto.getServiceIds()));
		Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()));
		Set<Condition> conditions = new HashSet<>(conditionRepository.findAllById(dto.getConditionIds()));

		Office office = officeMapper.toEntity(dto, company, services, categories, conditions, user);
		return officeMapper.toDto(officeRepository.save(office));
	}

	@Override
	public OfficeDto updateOffice(Long officeId, OfficeDto dto) {
		Office office = officeRepository.findById(officeId)
			.orElseThrow(() -> new ResourceNotFoundException("Office not found"));

		Set<ServiceOffice> services = new HashSet<>(serviceRepository.findAllById(dto.getServiceIds()));
		Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()));
		Set<Condition> conditions = new HashSet<>(conditionRepository.findAllById(dto.getConditionIds()));

		office.setWorkSchedule(dto.getWorkSchedule());
		office.setAdditionalDescription(dto.getAdditionalDescription());
		office.setLatitude(dto.getLatitude());
		office.setLongitude(dto.getLongitude());
		office.setLocationName(dto.getLocationName());
		office.setRegionId(dto.getRegionId());
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


	private User getUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
	}
}
