package org.ua.drmp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.CFieldStructure;

@Repository
public interface CFieldStructureRepository extends JpaRepository<CFieldStructure, Long> {
}
