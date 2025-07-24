package org.ua.drmp.feedback;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService{
	private final FeedbackRepository feedbackRepository;
	private final CompanyRepository companyRepository;
	private final FeedbackMapper feedbackMapper;
	@Override
	public void createFeedback(FeedbackRequest request) {
		feedbackRepository.save(Feedback.builder()
			.email(request.email())
			.name(request.name())
			.message(request.message())
			.build());
	}

	@Override
	public List<FeedbackViewDto> fetchAll() {
		return feedbackRepository.findAll().stream()
			.map(feedbackMapper::toViewDto)
			.toList();
	}

	@Override
	public void delete(Long id) {
		feedbackRepository.deleteById(id);
	}

	@Override
	public void assignCompany(Long id, AssignCompanyRequest request) {
		Feedback feedback = feedbackRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Feedback not found"));

		companyRepository.findById(request.companyId()).orElseThrow(() -> new ResourceNotFoundException("Company not found"));
		feedback.setCompanyId(request.companyId());
		feedbackRepository.save(feedback);
	}

	@Override
	public List<FeedbackViewDto> fetchAllByCompanyId(Long companyId) {
		return feedbackRepository.findAllByCompanyId(companyId).stream()
			.map(feedbackMapper::toViewDto)
			.toList();
	}
}
