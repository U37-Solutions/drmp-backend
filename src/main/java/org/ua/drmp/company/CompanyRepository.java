package org.ua.drmp.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
