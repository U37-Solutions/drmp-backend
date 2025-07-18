package org.ua.drmp.company.service;

import java.util.List;
import java.util.Optional;
import org.ua.drmp.company.dto.CompanyDto;
import org.ua.drmp.company.entity.CompanyStatus;

public interface CompanyService {
	List<CompanyDto> fetchAllCompanies();

	List<CompanyDto> fetchCompanyByStatus(Optional<CompanyStatus> status);

	CompanyDto getCompany(Long companyId);

	CompanyDto updateCompany(Long companyId, CompanyDto dto);

	void deleteCompany(Long companyId);

	void createCompany(CompanyDto companyDto);
}
