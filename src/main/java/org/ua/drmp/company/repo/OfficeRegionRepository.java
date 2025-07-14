package org.ua.drmp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.OfficeRegion;

@Repository
public interface OfficeRegionRepository extends JpaRepository<OfficeRegion, Long> {}