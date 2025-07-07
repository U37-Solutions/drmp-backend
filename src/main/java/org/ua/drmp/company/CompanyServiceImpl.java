package org.ua.drmp.company;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
	private final CompanyRepository companyRepository;
	@Override
	public void createCompany(CompanyDto companyDto) {
		Company company = new Company();
		company.setName(companyDto.name());
		companyRepository.save(company);
	}
}
