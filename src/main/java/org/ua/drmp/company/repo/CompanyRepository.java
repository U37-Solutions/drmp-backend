package org.ua.drmp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.service.CompanyRepositoryCustom;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, CompanyRepositoryCustom {
}
