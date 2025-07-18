package org.ua.drmp.company.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.CompanyStatus;
import org.ua.drmp.company.service.CompanyRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

	@PersistenceContext
	private final EntityManager entityManager;

	@Override
	public List<Company> findCompaniesByStatus(Optional<CompanyStatus> status) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Company> query = cb.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		List<Predicate> predicates = new ArrayList<>();

		status.ifPresent(s -> predicates.add(cb.equal(root.get("status"), s)));

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		return entityManager.createQuery(query).getResultList();
	}
}
