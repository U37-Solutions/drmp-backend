package org.ua.drmp.company.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ua.drmp.company.dto.CustomFieldValueDto;
import org.ua.drmp.company.dto.OfficeDto;
import org.ua.drmp.company.dto.OfficeMapper;
import org.ua.drmp.company.entity.CFieldStructure;
import org.ua.drmp.company.entity.CFieldStructureType;
import org.ua.drmp.company.entity.CFieldValue;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.company.entity.OfficeCFieldValue;
import org.ua.drmp.company.entity.ServiceOffice;
import org.ua.drmp.company.repo.CFieldStructureRepository;
import org.ua.drmp.company.repo.CFieldValueRepository;
import org.ua.drmp.company.repo.CategoryRepository;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.company.repo.ConditionRepository;
import org.ua.drmp.company.repo.OfficeCFieldValueRepository;
import org.ua.drmp.company.repo.OfficeRepository;
import org.ua.drmp.company.repo.ServiceRepository;
import org.ua.drmp.company.service.OfficeService;
import org.ua.drmp.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {

	private final OfficeRepository officeRepository;
	private final CompanyRepository companyRepository;
	private final OfficeMapper officeMapper;
	private final ServiceRepository serviceRepository;
	private final CategoryRepository categoryRepository;
	private final ConditionRepository conditionRepository;
	private final CFieldValueRepository cFieldValueRepository;
	private final CFieldStructureRepository cFieldStructureRepository;
	private final OfficeCFieldValueRepository officeCFieldValueRepository;
	@PersistenceContext
	private EntityManager entityManager;

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
	public List<OfficeDto> fetchAllOffices() {
		return officeRepository.findAll().stream().map(officeMapper::toDto).toList();
	}

	@Override
	public OfficeDto createOffice(OfficeDto dto) {
		Long companyId = dto.getCompanyId();
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

		Set<ServiceOffice> services = new HashSet<>(serviceRepository.findAllById(dto.getServiceIds()));
		Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()));
		Set<Condition> conditions = new HashSet<>(conditionRepository.findAllById(dto.getConditionIds()));
		Set<CFieldValue> customFieldValues = resolveCustomFieldValues(dto.getCustomFields());

		Office office = officeMapper.toEntityWithoutUser(dto, company, services, categories, conditions, Set.of());
		Office savedOffice = officeRepository.save(office);

		List<OfficeCFieldValue> officeCFieldValues = customFieldValues.stream()
			.map(cf -> OfficeCFieldValue.builder()
				.office(savedOffice)
				.value(cf)
				.build())
			.collect(Collectors.toList());

		officeCFieldValueRepository.saveAll(officeCFieldValues);

		savedOffice.setCustomFieldValues(officeCFieldValues);

		return officeMapper.toDto(savedOffice);
	}

	@Override
	@Transactional
	public OfficeDto updateOffice(Long officeId, OfficeDto dto) {
		Office existingOffice = officeRepository.findById(officeId)
			.orElseThrow(() -> new ResourceNotFoundException("Office not found with id: " + officeId));

		if (dto.getCompanyId() == null) {
			throw new ResourceNotFoundException("CompanyId must not be null");
		}
		Company company = companyRepository.findById(dto.getCompanyId())
			.orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + dto.getCompanyId()));

		Set<ServiceOffice> services = new HashSet<>(serviceRepository.findAllById(dto.getServiceIds()));
		Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()));
		Set<Condition> conditions = new HashSet<>(conditionRepository.findAllById(dto.getConditionIds()));

		// Отримати нові CFieldValue з уникненням дублювання
		Set<CFieldValue> newCustomFieldValues = resolveCustomFieldValues(dto.getCustomFields())
			.stream()
			.map(cf -> cFieldValueRepository.findById(cf.getId())
				.orElseThrow(() -> new ResourceNotFoundException("CFieldValue not found with id: " + cf.getId())))
			.collect(Collectors.toSet());

		Office updatedOffice = officeMapper.toEntityWithoutUser(dto, company, services, categories, conditions, Set.of());
		updatedOffice.setId(existingOffice.getId());
		updatedOffice.setUser(existingOffice.getUser());

		Office savedOffice = officeRepository.save(updatedOffice);

		// 1. Видалити старі зв’язки
		List<OfficeCFieldValue> existingLinks = officeCFieldValueRepository.findAllByOffice(savedOffice);
		officeCFieldValueRepository.deleteAllInBatch(existingLinks);
		entityManager.flush();
		entityManager.clear();

		// 2. Додати нові зв’язки
		List<OfficeCFieldValue> newLinks = newCustomFieldValues.stream()
			.map(cf -> OfficeCFieldValue.builder()
				.office(savedOffice)
				.value(cf)
				.build())
			.collect(Collectors.toList());

		officeCFieldValueRepository.saveAll(newLinks);

		// 3. Сетнути нові значення (не обов'язково, якщо не використовуєш далі)
		savedOffice.setCustomFieldValues(newLinks);

		return officeMapper.toDto(savedOffice);
	}

	@Transactional
	@Override
	public void deleteOffice(Long officeId) {
		Office office = officeRepository.findById(officeId)
			.orElseThrow(() -> new ResourceNotFoundException("Cannot find office with id: " + officeId));

		// Отримуємо всі CFieldValue, які були в цьому офісі
		List<CFieldValue> valuesToMaybeDelete = office.getCustomFieldValues().stream()
			.map(OfficeCFieldValue::getValue)
			.toList();

		officeRepository.delete(office);

		// Перевіряємо, чи залишилися посилання на ці CFieldValue
		for (CFieldValue value : valuesToMaybeDelete) {
			long count = officeCFieldValueRepository.countByValueId(value.getId());
			if (count == 0) {
				cFieldValueRepository.delete(value);
			}
		}
	}


	private Set<CFieldValue> resolveCustomFieldValues(List<CustomFieldValueDto> dtos) {
		if (dtos == null || dtos.isEmpty()) {
			return Set.of();
		}

		return dtos.stream()
			.map(dto -> cFieldValueRepository.findByStructureIdAndValue(dto.getStructureId(), dto.getValue())
				.orElseGet(() -> {
					CFieldStructure structure = cFieldStructureRepository.findById(dto.getStructureId())
						.orElseThrow(() -> new ResourceNotFoundException("CFieldStructure not found: " + dto.getStructureId()));

					if (structure.getType() == CFieldStructureType.SELECT) {
						List<String> options = structure.getOptions();
						if (options == null || !options.contains(dto.getValue())) {
							throw new ResourceNotFoundException("Invalid value for SELECT field: " + dto.getValue());
						}
					}

					CFieldValue newValue = CFieldValue.builder()
						.structure(structure)
						.value(dto.getValue())
						.build();

					return cFieldValueRepository.save(newValue);
				}))
			.collect(Collectors.toSet());
	}

}
