package org.ua.drmp.company.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company-register")
public class CompanyRegistrationController {

	private final CompanyRegistrationService registrationService;

	@PostMapping
	public ResponseEntity<Void> register(@RequestBody CompanyRegisterRequest request) {
		registrationService.registerCompany(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/approve")
	public ResponseEntity<Void> approveCompany(@PathVariable Long id) {
		registrationService.approveCompany(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/reject")
	public ResponseEntity<Void> rejectCompany(@PathVariable Long id,
		@RequestBody RejectCompanyRequest request) {
		registrationService.rejectCompany(id, request);
		return ResponseEntity.ok().build();
	}

}
