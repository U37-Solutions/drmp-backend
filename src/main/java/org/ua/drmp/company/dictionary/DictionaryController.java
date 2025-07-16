package org.ua.drmp.company.dictionary;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.CompanyType;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.ServiceOffice;

@RestController
@RequestMapping("/dictionary")
@RequiredArgsConstructor
public class DictionaryController {

	private final DictionaryService dictionaryService;

	@GetMapping("/services")
	public ResponseEntity<List<ServiceOffice>> fetchAllServices() {
		return ResponseEntity.ok(dictionaryService.fetchAllServices());
	}

	@GetMapping("/services/{id}")
	public ResponseEntity<ServiceOffice> fetchServiceById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.fetchServiceById(id));
	}

	@PostMapping("/services")
	public ResponseEntity<ServiceOffice> createNewServiceOffice(@RequestBody String name) {
		dictionaryService.createNewService(name);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/services/{id}")
	public ResponseEntity<Void> deleteServiceById(@PathVariable Long id){
		dictionaryService.deleteServiceById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/categories")
	public ResponseEntity<List<Category>> fetchAllCategories() {
		return ResponseEntity.ok(dictionaryService.fetchAllCategories());
	}

	@GetMapping("/categories/{id}")
	public ResponseEntity<Category> fetchCategoryById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.fetchCategoryById(id));
	}

	@PostMapping("/categories")
	public ResponseEntity<Category> createNewCategory(@RequestBody String name) {
		dictionaryService.createNewCategory(name);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/categories/{id}")
	public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id){
		dictionaryService.deleteCategoryById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/conditions")
	public ResponseEntity<List<Condition>> fetchAllConditions() {
		return ResponseEntity.ok(dictionaryService.fetchAllConditions());
	}

	@GetMapping("/conditions/{id}")
	public ResponseEntity<Condition> fetchConditionById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.fetchConditionById(id));
	}

	@PostMapping("/conditions")
	public ResponseEntity<Condition> createNewCondition(@RequestBody String name) {
		dictionaryService.createNewCondition(name);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/conditions/{id}")
	public ResponseEntity<Void> deleteConditionById(@PathVariable Long id){
		dictionaryService.deleteConditionById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/company-types")
	public ResponseEntity<List<CompanyType>> fetchAllCompanyTypes() {
		return ResponseEntity.ok(dictionaryService.fetchAllCompanyTypes());
	}

	@GetMapping("/company-types/{id}")
	public ResponseEntity<CompanyType> fetchCompanyTypeById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.fetchCompanyTypeById(id));
	}

	@PostMapping("/company-types")
	public ResponseEntity<CompanyType> createNewCompanyType(@RequestBody String name) {
		dictionaryService.createNewCompanyType(name);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/company-types/{id}")
	public ResponseEntity<Void> deleteCompanyTypeById(@PathVariable Long id){
		dictionaryService.deleteCompanyTypeById(id);
		return ResponseEntity.noContent().build();
	}
}
