package org.ua.drmp.open;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.Office;

@Repository
@RequiredArgsConstructor
public class PublicOfficeRepositoryImpl implements PublicOfficeRepositoryCustom {

	private final EntityManager entityManager;

	@Override
	public List<Office> findOfficesWithinBounds(double lat1, double lon1, double lat2, double lon2) {
		String query = """
            SELECT o FROM Office o
            WHERE o.coordinates IS NOT NULL
              AND within(o.coordinates, ST_MakeEnvelope(:lon1, :lat1, :lon2, :lat2, 4326)) = true
        """;
		return entityManager.createQuery(query, Office.class)
			.setParameter("lat1", Math.min(lat1, lat2))
			.setParameter("lat2", Math.max(lat1, lat2))
			.setParameter("lon1", Math.min(lon1, lon2))
			.setParameter("lon2", Math.max(lon1, lon2))
			.getResultList();
	}
}

