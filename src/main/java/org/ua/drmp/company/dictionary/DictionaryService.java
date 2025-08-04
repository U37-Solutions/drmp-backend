package org.ua.drmp.company.dictionary;

import jakarta.validation.Valid;
import java.util.List;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.CompanyType;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.ServiceOffice;

public interface DictionaryService {
	void createNewService(List<String> names);
	List<ServiceOffice> fetchAllServices();

	ServiceOffice fetchServiceById(Long id);

	void createNewCategory(List<String> names);

	List<Category> fetchAllCategories();

	Category fetchCategoryById(Long id);

	void createNewCondition(List<String> names);

	List<Condition> fetchAllConditions();

	Condition fetchConditionById(Long id);

	void createNewCompanyType(List<String> names);

	List<CompanyType> fetchAllCompanyTypes();

	CompanyType fetchCompanyTypeById(Long id);

	void deleteServicesByIds(List<Long> ids);
	void deleteCategoriesByIds(List<Long> ids);
	void deleteConditionsByIds(List<Long> ids);
	void deleteCompanyTypesByIds(List<Long> ids);


	void updateServices(List<@Valid IdNameDto> dtos);
	void updateCategories(List<@Valid IdNameDto> dtos);
	void updateConditions(List<@Valid IdNameDto> dtos);
	void updateCompanyTypes(List<@Valid IdNameDto> dtos);
}
