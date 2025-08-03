package org.ua.drmp.open;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.dto.RegionConst;
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

	@Override
	public List<Office> searchWithFilters(String searchBy, String search,
		String regionName, String city,
		List<String> categories, List<String> services,
		Boolean isFree,
		Optional<double[]> boundaries) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Office> query = cb.createQuery(Office.class);
		Root<Office> office = query.from(Office.class);
		query.select(office).distinct(true);

		List<Predicate> predicates = new ArrayList<>();

		if (search != null && !search.isBlank()) {
			if ("name".equalsIgnoreCase(searchBy)) {
				predicates.add(cb.like(cb.lower(office.get("company").get("name")), "%" + search.toLowerCase() + "%"));
			} else if ("address".equalsIgnoreCase(searchBy)) {
				predicates.add(cb.like(cb.lower(office.get("locationName")), "%" + search.toLowerCase() + "%"));
			}
		}

		if (regionName != null) {
			RegionConst.regions.entrySet().stream()
				.filter(entry -> entry.getValue().equalsIgnoreCase(regionName))
				.map(Map.Entry::getKey)
				.findFirst().ifPresent(regionId -> predicates.add(cb.equal(office.get("regionId"), regionId)));
		}

		if (city != null) {
			predicates.add(cb.equal(cb.lower(office.get("city")), city.toLowerCase()));
		}

		if (isFree != null) {
			predicates.add(cb.equal(office.get("isFree"), isFree));
		}

		if (categories != null && !categories.isEmpty()) {
			Join<Object, Object> catJoin = office.join("categories");
			predicates.add(catJoin.get("name").in(categories));
		}

		if (services != null && !services.isEmpty()) {
			Join<Object, Object> srvJoin = office.join("services");
			predicates.add(srvJoin.get("name").in(services));
		}

		// Якщо немає жодного фільтра — додаємо пошук по координатах
		if (search == null && regionName == null && city == null
			&& (categories == null || categories.isEmpty())
			&& (services == null || services.isEmpty())
			&& isFree == null
			&& boundaries.isPresent() && boundaries.get().length == 4) {

			double[] b = boundaries.get();
			Expression<Boolean> within = cb.function("ST_Within", Boolean.class,
				office.get("coordinates"),
				cb.function("ST_MakeEnvelope", Object.class,
					cb.literal(Math.min(b[1], b[3])), // lon1
					cb.literal(Math.min(b[0], b[2])), // lat1
					cb.literal(Math.max(b[1], b[3])), // lon2
					cb.literal(Math.max(b[0], b[2])),
					cb.literal(4326))
			);
			predicates.add(cb.isTrue(within));
		}

		query.where(cb.and(predicates.toArray(new Predicate[0])));

		return entityManager.createQuery(query).getResultList();
	}

}

