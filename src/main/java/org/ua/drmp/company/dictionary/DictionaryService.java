package org.ua.drmp.company.dictionary;

import java.util.List;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.CompanyType;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.ServiceOffice;

public interface DictionaryService {
	void createNewService(String name);
	List<ServiceOffice> fetchAllServices();

	ServiceOffice fetchServiceById(Long id);

	void deleteServiceById(Long id);

	void createNewCategory(String name);

	List<Category> fetchAllCategories();

	Category fetchCategoryById(Long id);

	void deleteCategoryById(Long id);

	void createNewCondition(String name);

	List<Condition> fetchAllConditions();

	Condition fetchConditionById(Long id);

	void deleteConditionById(Long id);

	void createNewCompanyType(String name);

	List<CompanyType> fetchAllCompanyTypes();

	CompanyType fetchCompanyTypeById(Long id);

	void deleteCompanyTypeById(Long id);

}
