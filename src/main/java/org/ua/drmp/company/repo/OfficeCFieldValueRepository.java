package org.ua.drmp.company.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.company.entity.OfficeCFieldValue;
import org.ua.drmp.company.entity.OfficeCFieldValueId;

@Repository
public interface OfficeCFieldValueRepository extends JpaRepository<OfficeCFieldValue, OfficeCFieldValueId> {

	List<OfficeCFieldValue> findAllByOffice(Office office);


	@Query("SELECT COUNT(ocfv) FROM OfficeCFieldValue ocfv WHERE ocfv.value.id = :valueId")
	long countByValueId(@Param("valueId") Long valueId);

	@Modifying
	@Query("""
		delete from OfficeCFieldValue ocfv
		where ocfv.value.id in (
			select cv.id
			from CFieldValue cv
			where cv.structure.id = :structureId
		)
	""")
	void deleteAllByStructureId(@Param("structureId") Long structureId);
}