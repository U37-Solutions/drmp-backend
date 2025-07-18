package org.ua.drmp.company;

import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.company.dto.OfficeDto;
import org.ua.drmp.company.service.OfficeService;

@RestController
@RequestMapping("/offices")
@RequiredArgsConstructor
public class OfficeController {
	private final OfficeService officeService;

	// ADMIN, EDITOR, COMPANY_ADMIN, COMPANY_USER
	@PreAuthorize("@userSecurity.isCompanyUserOrAbove(authentication, #companyId)")
	@GetMapping("/company/{companyId}")
	public ResponseEntity<List<OfficeDto>> getAllOfficesForCompany(@PathVariable Long companyId) {
		return ResponseEntity.ok(officeService.fetchAllOfficeByCompanyId(companyId));
	}

	@PreAuthorize("@userSecurity.isCompanyUserOrAbove(authentication, #id)")
	@GetMapping("/{id}")
	public ResponseEntity<OfficeDto> getOffice(@PathVariable Long id) {
		return ResponseEntity.ok(officeService.getOffice(id));
	}

	@PreAuthorize("@userSecurity.isCompanyUserOrAbove(authentication, #companyId)")
	@PostMapping("/company/{companyId}")
	public ResponseEntity<OfficeDto> createOffice(@PathVariable Long companyId,
		@RequestBody OfficeDto dto) {
		return ResponseEntity.ok(officeService.createOffice(companyId, dto));
	}

	@PreAuthorize("@userSecurity.isCompanyUserOrAbove(authentication, #id)")
	@PutMapping("/{id}")
	public ResponseEntity<OfficeDto> updateOffice(@PathVariable Long id,
		@RequestBody OfficeDto dto) {
		return ResponseEntity.ok(officeService.updateOffice(id, dto));
	}

	@PreAuthorize("@userSecurity.isCompanyUserOrAbove(authentication, #id)")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
		officeService.deleteOffice(id);
		return ResponseEntity.noContent().build();
	}
}
