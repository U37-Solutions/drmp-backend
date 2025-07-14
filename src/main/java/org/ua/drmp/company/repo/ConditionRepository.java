package org.ua.drmp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ua.drmp.company.entity.Condition;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {}
