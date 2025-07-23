package org.ua.drmp.feedback;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

public interface FeedbackService {
	void createFeedback(@RequestBody @Valid FeedbackRequest request);

	List<FeedbackViewDto> fetchAll();

	void delete(Long id);

	void assignCompany(Long id, @Valid AssignCompanyRequest request);

	List<FeedbackViewDto> fetchAllByCompanyId(Long companyId);
}
