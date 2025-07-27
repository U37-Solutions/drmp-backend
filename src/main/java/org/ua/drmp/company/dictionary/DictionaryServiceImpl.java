package org.ua.drmp.company.dictionary;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.CompanyType;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.ServiceOffice;
import org.ua.drmp.company.repo.CategoryRepository;
import org.ua.drmp.company.repo.CompanyTypeRepository;
import org.ua.drmp.company.repo.ConditionRepository;
import org.ua.drmp.company.repo.ServiceRepository;
import org.ua.drmp.exception.BadRequestException;
import org.ua.drmp.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

	private final ServiceRepository serviceRepository;
	private final CategoryRepository categoryRepository;

	private final ConditionRepository conditionRepository;

	private final CompanyTypeRepository companyTypeRepository;

	@Override
	public void createNewService(String name) {
		ServiceOffice serviceOffice = new ServiceOffice();
		serviceOffice.setName(name);
		serviceRepository.save(serviceOffice);
	}

	@Override
	public List<ServiceOffice> fetchAllServices() {
		return serviceRepository.findAll();
	}

	@Override
	public ServiceOffice fetchServiceById(Long id) {
		return serviceRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Service not found"));
	}

	@Override
	public void deleteServiceById(Long id) {
		serviceRepository.deleteById(id);
	}

	@Override
	public void createNewCategory(String name) {
		Category category = new Category();
		category.setName(name);
		categoryRepository.save(category);
	}

	@Override
	public List<Category> fetchAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public Category fetchCategoryById(Long id) {
		return categoryRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
	}

	@Override
	public void deleteCategoryById(Long id) {
		categoryRepository.deleteById(id);
	}

	@Override
	public void createNewCondition(String name) {
		Condition condition = new Condition();
		condition.setName(name);
		conditionRepository.save(condition);
	}

	@Override
	public List<Condition> fetchAllConditions() {
		return conditionRepository.findAll();
	}

	@Override
	public Condition fetchConditionById(Long id) {
		return conditionRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Condition not found"));
	}

	@Override
	public void deleteConditionById(Long id) {
		conditionRepository.deleteById(id);
	}

	@Override
	public void createNewCompanyType(String name) {
		CompanyType companyType = new CompanyType();
		companyType.setName(name);
		companyTypeRepository.save(companyType);
	}

	@Override
	public List<CompanyType> fetchAllCompanyTypes() {
		return companyTypeRepository.findAll();
	}

	@Override
	public CompanyType fetchCompanyTypeById(Long id) {
		return companyTypeRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Company type not found"));
	}

	@Override
	public void deleteCompanyTypeById(Long id) {
		companyTypeRepository.deleteById(id);
	}

	@Override
	@Transactional
	public void updateServices(List<IdNameDto> dtos) {
		validateNoDuplicates(dtos);

		Map<Long, ServiceOffice> entitiesById = serviceRepository.findAllById(
				dtos.stream().map(IdNameDto::id).toList()
			).stream()
			.collect(Collectors.toMap(ServiceOffice::getId, Function.identity()));

		List<ServiceOffice> updated = dtos.stream().map(dto -> {
			ServiceOffice entity = entitiesById.get(dto.id());
			if (entity == null) {
				throw new ResourceNotFoundException("Service not found with id: " + dto.id());
			}
			entity.setName(dto.name());
			return entity;
		}).toList();

		serviceRepository.saveAll(updated);
	}

	@Override
	@Transactional
	public void updateCategories(List<IdNameDto> dtos) {
		validateNoDuplicates(dtos);

		Map<Long, Category> entitiesById = categoryRepository.findAllById(
				dtos.stream().map(IdNameDto::id).toList()
			).stream()
			.collect(Collectors.toMap(Category::getId, Function.identity()));

		List<Category> updated = dtos.stream().map(dto -> {
			Category entity = entitiesById.get(dto.id());
			if (entity == null) {
				throw new ResourceNotFoundException("Category not found with id: " + dto.id());
			}
			entity.setName(dto.name());
			return entity;
		}).toList();

		categoryRepository.saveAll(updated);
	}

	@Override
	@Transactional
	public void updateConditions(List<IdNameDto> dtos) {
		validateNoDuplicates(dtos);

		Map<Long, Condition> entitiesById = conditionRepository.findAllById(
				dtos.stream().map(IdNameDto::id).toList()
			).stream()
			.collect(Collectors.toMap(Condition::getId, Function.identity()));

		List<Condition> updated = dtos.stream().map(dto -> {
			Condition entity = entitiesById.get(dto.id());
			if (entity == null) {
				throw new ResourceNotFoundException("Condition not found with id: " + dto.id());
			}
			entity.setName(dto.name());
			return entity;
		}).toList();

		conditionRepository.saveAll(updated);
	}

	@Override
	@Transactional
	public void updateCompanyTypes(List<IdNameDto> dtos) {
		validateNoDuplicates(dtos);

		Map<Long, CompanyType> entitiesById = companyTypeRepository.findAllById(
				dtos.stream().map(IdNameDto::id).toList()
			).stream()
			.collect(Collectors.toMap(CompanyType::getId, Function.identity()));

		List<CompanyType> updated = dtos.stream().map(dto -> {
			CompanyType entity = entitiesById.get(dto.id());
			if (entity == null) {
				throw new ResourceNotFoundException("Company type not found with id: " + dto.id());
			}
			entity.setName(dto.name());
			return entity;
		}).toList();

		companyTypeRepository.saveAll(updated);
	}


	private void validateNoDuplicates(List<IdNameDto> dtos) {
		Set<Long> uniqueIds = new HashSet<>();
		for (IdNameDto dto : dtos) {
			if (!uniqueIds.add(dto.id())) {
				throw new BadRequestException("Duplicate ID in request: " + dto.id());
			}
		}
	}

}
