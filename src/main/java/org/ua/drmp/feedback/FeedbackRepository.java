package org.ua.drmp.feedback;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	List<Feedback> findAllByCompanyId(Long companyId);

}
