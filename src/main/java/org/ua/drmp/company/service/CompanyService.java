package org.ua.drmp.company.service;

import org.ua.drmp.company.dto.CompanyDto;

public interface CompanyService {
	CompanyDto getCompany(Long companyId);

	CompanyDto updateCompany(Long companyId, CompanyDto dto);

	void deleteCompany(Long companyId);

	void createCompany(CompanyDto companyDto);
}
