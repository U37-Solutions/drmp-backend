package org.ua.drmp.open;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ua.drmp.company.entity.Office;

public interface PublicOfficeRepository extends JpaRepository<Office, Long>, PublicOfficeRepositoryCustom {
	@Query("SELECT o FROM Office o WHERE o.coordinates IS NOT NULL")
	List<Office> findAllWithCoordinates();
}
