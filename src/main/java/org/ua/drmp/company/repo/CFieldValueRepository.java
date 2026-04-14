package org.ua.drmp.company.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.CFieldValue;

@Repository
public interface CFieldValueRepository extends JpaRepository<CFieldValue, Long> {

	@Query(
		value = "SELECT * FROM cfield_values WHERE cfield_structures_id = :structureId AND value = :value LIMIT 1",
		nativeQuery = true
	)
	Optional<CFieldValue> findFirstByStructureIdAndValue(@Param("structureId") Long structureId,
		@Param("value") String value);

	@Modifying
	@Query("delete from CFieldValue v where v.structure.id = :structureId")
	void deleteByStructureId(@Param("structureId") Long structureId);
}
