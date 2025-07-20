package org.ua.drmp.company.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.CFieldValue;

@Repository
public interface CFieldValueRepository extends JpaRepository<CFieldValue, Long> {

	@Query("SELECT v FROM CFieldValue v WHERE v.structure.id = :structureId AND v.value = :value")
	Optional<CFieldValue> findByStructureIdAndValue(@Param("structureId") Long structureId,
		@Param("value") String value);

}
