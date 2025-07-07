package org.ua.drmp.company;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

	private final CompanyService companyService;

	@PostMapping
	void createCompany(@RequestBody CompanyDto companyDto) {
		companyService.createCompany(companyDto);
	}

}
