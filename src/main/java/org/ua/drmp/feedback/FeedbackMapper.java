package org.ua.drmp.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.exception.ResourceNotFoundException;

@Component
@RequiredArgsConstructor
public class FeedbackMapper {

	private final CompanyRepository companyRepository;

	public FeedbackViewDto toViewDto(Feedback feedback) {
		String companyName = null;

		if (feedback.getCompanyId() != null) {
			Company company = companyRepository.findById(feedback.getCompanyId())
				.orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + feedback.getCompanyId()));
			companyName = company.getName();
		}

		return FeedbackViewDto.builder()
			.id(feedback.getId())
			.email(feedback.getEmail())
			.name(feedback.getName())
			.message(feedback.getMessage())
			.companyId(feedback.getCompanyId())
			.companyName(companyName)
			.build();
	}
}
