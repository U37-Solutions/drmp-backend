package org.ua.drmp.company;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.company.dto.CompanyDto;
import org.ua.drmp.company.entity.CompanyStatus;
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

	// ADMIN, EDITOR
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	@GetMapping("/all")
	public ResponseEntity<List<CompanyDto>> fetchAllCompanies() {
		return ResponseEntity.ok(companyService.fetchAllCompanies());
	}

	// ADMIN, EDITOR
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	@GetMapping
	public ResponseEntity<List<CompanyDto>> getCompanies(@RequestParam(name = "status", required = false) CompanyStatus status) {
		return ResponseEntity.ok(companyService.fetchCompanyByStatus(Optional.ofNullable(status)));
	}

	// ADMIN, EDITOR, COMPANY_ADMIN (власник компанії)
	@PreAuthorize("@userSecurity.isAdminEditorOrCompanyAdmin(authentication, #id)")
	@GetMapping("/{id}")
	public ResponseEntity<CompanyDto> getCompany(@PathVariable Long id) {
		return ResponseEntity.ok(companyService.getCompany(id));
	}

	// ADMIN або COMPANY_ADMIN (власник компанії)
	@PreAuthorize("@userSecurity.isAdminEditorOrCompanyAdmin(authentication, #id)")
	@PutMapping("/{id}")
	public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id,
		@RequestBody CompanyDto dto) {
		return ResponseEntity.ok(companyService.updateCompany(id, dto));
	}

	// ADMIN або COMPANY_ADMIN (власник компанії)
	@PreAuthorize("@userSecurity.isAdminOrCompanyAdmin(authentication, #id)")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
		companyService.deleteCompany(id);
		return ResponseEntity.noContent().build();
	}

}
