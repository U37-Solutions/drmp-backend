package org.ua.drmp.company.service;

import java.util.List;
import java.util.Optional;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.CompanyStatus;

public interface CompanyRepositoryCustom {
	List<Company> findCompaniesByStatus(Optional<CompanyStatus> status);
}
