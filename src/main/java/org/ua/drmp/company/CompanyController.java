package org.ua.drmp.company;

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
import org.ua.drmp.company.dto.CompanyDto;
import org.ua.drmp.company.service.CompanyService;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

	private final CompanyService companyService;

	@PostMapping
	void createCompany(@RequestBody CompanyDto companyDto) {
		companyService.createCompany(companyDto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CompanyDto> getCompany(@PathVariable Long id) {
		return ResponseEntity.ok(companyService.getCompany(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id,
		@RequestBody CompanyDto dto) {
		return ResponseEntity.ok(companyService.updateCompany(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
		companyService.deleteCompany(id);
		return ResponseEntity.noContent().build();
	}

}
