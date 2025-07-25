package org.ua.drmp.company;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.company.dto.PublicCompanyDto;
import org.ua.drmp.company.service.CompanyService;

@RestController
@RequestMapping("/public/companies")
@RequiredArgsConstructor
public class PublicCompanyController {
	private final CompanyService companyService;

	@GetMapping
	public ResponseEntity<List<PublicCompanyDto>> fetchAllPublicCompanies() {
		return ResponseEntity.ok(companyService.fetchAllPublicCompanies());
	}
}
