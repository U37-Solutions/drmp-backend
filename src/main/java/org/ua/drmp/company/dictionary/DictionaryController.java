package org.ua.drmp.company.dictionary;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	public static final String SERVICES_ENDPOINT = "/services";
	public static final String CATEGORIES_ENDPOINT = "/categories";
	public static final String CONDITIONS_ENDPOINT = "/conditions";
	public static final String COMPANY_TYPES_ENDPOINT = "/company-types";
	private final DictionaryService dictionaryService;

	@GetMapping(SERVICES_ENDPOINT)
	public ResponseEntity<List<ServiceOffice>> fetchAllServices() {
		return ResponseEntity.ok(dictionaryService.fetchAllServices());
	}

	@GetMapping(SERVICES_ENDPOINT + "/{id}")
	public ResponseEntity<ServiceOffice> fetchServiceById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.fetchServiceById(id));
	}

	@PostMapping(SERVICES_ENDPOINT)
	public ResponseEntity<ServiceOffice> createNewServiceOffice(@RequestBody String name) {
		dictionaryService.createNewService(name);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(SERVICES_ENDPOINT)
	public ResponseEntity<Void> updateServices(@RequestBody List<IdNameDto> dtos) {
		dictionaryService.updateServices(dtos);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(SERVICES_ENDPOINT + "/{id}")
	public ResponseEntity<Void> deleteServiceById(@PathVariable Long id){
		dictionaryService.deleteServiceById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(CATEGORIES_ENDPOINT)
	public ResponseEntity<List<Category>> fetchAllCategories() {
		return ResponseEntity.ok(dictionaryService.fetchAllCategories());
	}

	@GetMapping(CATEGORIES_ENDPOINT + "/{id}")
	public ResponseEntity<Category> fetchCategoryById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.fetchCategoryById(id));
	}

	@PostMapping(CATEGORIES_ENDPOINT)
	public ResponseEntity<Category> createNewCategory(@RequestBody String name) {
		dictionaryService.createNewCategory(name);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(CATEGORIES_ENDPOINT)
	public ResponseEntity<Void> updateCategories(@RequestBody List<IdNameDto> dtos) {
		dictionaryService.updateCategories(dtos);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(CATEGORIES_ENDPOINT + "/{id}")
	public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id){
		dictionaryService.deleteCategoryById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(CONDITIONS_ENDPOINT)
	public ResponseEntity<List<Condition>> fetchAllConditions() {
		return ResponseEntity.ok(dictionaryService.fetchAllConditions());
	}

	@GetMapping(CONDITIONS_ENDPOINT + "/{id}")
	public ResponseEntity<Condition> fetchConditionById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.fetchConditionById(id));
	}

	@PostMapping(CONDITIONS_ENDPOINT)
	public ResponseEntity<Condition> createNewCondition(@RequestBody String name) {
		dictionaryService.createNewCondition(name);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(CONDITIONS_ENDPOINT)
	public ResponseEntity<Void> updateConditions(@RequestBody List<IdNameDto> dtos) {
		dictionaryService.updateConditions(dtos);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(CONDITIONS_ENDPOINT + "/{id}")
	public ResponseEntity<Void> deleteConditionById(@PathVariable Long id){
		dictionaryService.deleteConditionById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(COMPANY_TYPES_ENDPOINT)
	public ResponseEntity<List<CompanyType>> fetchAllCompanyTypes() {
		return ResponseEntity.ok(dictionaryService.fetchAllCompanyTypes());
	}

	@GetMapping(COMPANY_TYPES_ENDPOINT + "/{id}")
	public ResponseEntity<CompanyType> fetchCompanyTypeById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.fetchCompanyTypeById(id));
	}

	@PostMapping(COMPANY_TYPES_ENDPOINT)
	public ResponseEntity<CompanyType> createNewCompanyType(@RequestBody String name) {
		dictionaryService.createNewCompanyType(name);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(COMPANY_TYPES_ENDPOINT)
	public ResponseEntity<Void> updateCompanyTypes(@RequestBody List<IdNameDto> dtos) {
		dictionaryService.updateCompanyTypes(dtos);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(COMPANY_TYPES_ENDPOINT + "/{id}")
	public ResponseEntity<Void> deleteCompanyTypeById(@PathVariable Long id){
		dictionaryService.deleteCompanyTypeById(id);
		return ResponseEntity.noContent().build();
	}
}
