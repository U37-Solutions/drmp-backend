package org.ua.drmp.company.dictionary;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.CompanyType;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.ServiceOffice;
import org.ua.drmp.company.repo.CategoryRepository;
import org.ua.drmp.company.repo.CompanyTypeRepository;
import org.ua.drmp.company.repo.ConditionRepository;
import org.ua.drmp.company.repo.ServiceRepository;
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
}
