package org.ua.drmp.company.registration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.dto.CompanyMapper;
import org.ua.drmp.company.dto.CustomFieldValueDto;
import org.ua.drmp.company.dto.OfficeMapper;
import org.ua.drmp.company.entity.CFieldValue;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.CompanyStatus;
import org.ua.drmp.company.entity.CompanyType;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.company.entity.ServiceOffice;
import org.ua.drmp.company.repo.CFieldValueRepository;
import org.ua.drmp.company.repo.CategoryRepository;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.company.repo.CompanyTypeRepository;
import org.ua.drmp.company.repo.ConditionRepository;
import org.ua.drmp.company.repo.ServiceRepository;
import org.ua.drmp.entity.DRMPRole;
import org.ua.drmp.entity.Role;
import org.ua.drmp.entity.User;
import org.ua.drmp.exception.BadRequestException;
import org.ua.drmp.exception.ResourceNotFoundException;
import org.ua.drmp.logging.ChangeLogService;
import org.ua.drmp.repo.RoleRepository;
import org.ua.drmp.repo.UserRepository;
import org.ua.drmp.service.EmailService;

@Service
@RequiredArgsConstructor
public class CompanyRegistrationServiceImpl implements  CompanyRegistrationService {
	private final CompanyRepository companyRepository;
	private final CompanyTypeRepository companyTypeRepository;
	private final OfficeMapper officeMapper;
	private final CompanyMapper companyMapper;
	private final EmailService emailService;
	private final ServiceRepository serviceRepository;
	private final CategoryRepository categoryRepository;
	private final ConditionRepository conditionRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final CFieldValueRepository cFieldValueRepository;
	private final ChangeLogService changelogService;

	public void registerCompany(CompanyRegisterRequest request) {
		if (request.getOffices() == null || request.getOffices().isEmpty()) {
			throw new BadRequestException("Компанія повинна мати хоча б один офіс");
		}

		CompanyType type = companyTypeRepository.findById(request.getCompanyTypeId())
			.orElseThrow(() -> new ResourceNotFoundException("Тип компанії не знайдено"));

		Company company = Company.builder()
			.name(request.getName())
			.code(request.getCode())
			.contactName(request.getContactName())
			.ownershipType(request.getOwnershipType())
			.donorSupport(request.getDonorSupport())
			.phone(request.getPhone())
			.email(request.getEmail())
			.status(CompanyStatus.REVIEW)
			.companyType(type)
			.socials(new ArrayList<>())
			.offices(new ArrayList<>())
			.build();

		// Додаємо соц.мережі
		if (request.getSocials() != null) {
			request.getSocials().forEach(s ->
				company.getSocials().add(companyMapper.toSocialEntity(s, company))
			);
		}

		// Додаємо офіси
		request.getOffices().forEach(o -> {
			Set<ServiceOffice> services = new HashSet<>(serviceRepository.findAllById(o.getServiceIds()));
			Set<Category> categories = new HashSet<>(categoryRepository.findAllById(o.getCategoryIds()));
			Set<Condition> conditions = new HashSet<>(conditionRepository.findAllById(o.getConditionIds()));
			Set<CFieldValue> customFieldValues = resolveCustomFieldValues(o.getCustomFields());
			Office office = officeMapper.toEntityWithoutUser(o, company, services, categories, conditions, customFieldValues);
			company.getOffices().add(office);
		});

		companyRepository.save(company);

		// Відправка email адміну
		emailService.sendCompanyRegistrationNotification(company);
	}

	@Override
	public void approveCompany(Long companyId) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		if (!company.getUsers().isEmpty()) {
			throw new BadRequestException("Company already approved");
		}

		if (userRepository.existsByEmail(company.getEmail())) {
			throw new BadRequestException("User with this email already exists");
		}

		String rawPassword = RandomStringUtils.randomAlphanumeric(10);
		String encodedPassword = passwordEncoder.encode(rawPassword);
		String[] nameParts = company.getContactName().trim().split(" ", 2);
		String firstName = nameParts.length > 0 ? nameParts[0] : "";
		String lastName = nameParts.length > 1 ? nameParts[1] : "";

		User user = User.builder()
			.email(company.getEmail())
			.firstName(firstName)
			.lastName(lastName)
			.password(encodedPassword)
			.roles(Set.of(setCompanyAdminRole()))
			.company(company)
			.build();

		try {
			changelogService.logUserChange(
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"create",
				null,
				user.toJson()
			);
			userRepository.save(user);
		} catch (DataIntegrityViolationException ex) {
			throw new BadRequestException("User with this email already exists");
		}

		company.setStatus(CompanyStatus.ACTIVE);
		Company savedCompany = companyRepository.save(company);

		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		changelogService.logCompanyChange(
			savedCompany.getId(),
			email,
			"create",
			null,
			savedCompany.toJson()
		);

		emailService.sendAccountCredentialsEmail(user.getEmail(), rawPassword);
	}


	@Override
	public void rejectCompany(Long companyId, RejectCompanyRequest request) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		if (company.getStatus() == CompanyStatus.REJECTED) {
			throw new BadRequestException("Company already rejected");
		}

		if (company.getStatus() == CompanyStatus.ACTIVE) {
			throw new BadRequestException("Company already active");
		}
		company.setStatus(CompanyStatus.REJECTED);
		companyRepository.save(company);

		String editorContact = null;
		if (request.assignedEditorId() != null) {
			User editor = userRepository.findById(request.assignedEditorId())
				.orElseThrow(() -> new ResourceNotFoundException("Editor not found"));
			editorContact = editor.getEmail();
		}

		emailService.sendCompanyRejectionEmail(
			company.getEmail(),
			company.getName(),
			request.message(),
			editorContact
		);
	}

	private Role setCompanyAdminRole() {
		return roleRepository.findByName(DRMPRole.COMPANY_ADMIN)
			.orElseThrow(() -> new ResourceNotFoundException("Role COMPANY_USER not found"));
	}

	private Set<CFieldValue> resolveCustomFieldValues(List<CustomFieldValueDto> dtos) {
		if (dtos == null || dtos.isEmpty()) {
			return Set.of();
		}

		return dtos.stream()
			.map(f -> cFieldValueRepository.findByStructureIdAndValue(f.getStructureId(), f.getValue())
				.orElseThrow(() -> new ResourceNotFoundException("CFieldValue not found for structureId: " + f.getStructureId() + " and value: " + f.getValue())))
			.collect(Collectors.toSet());
	}

}
