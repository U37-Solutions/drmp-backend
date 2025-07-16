package org.ua.drmp.company.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
	List<Office> findAllByCompanyId(Long companyId);

}
