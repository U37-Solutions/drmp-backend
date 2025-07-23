package org.ua.drmp.feedback;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {
	private final FeedbackService feedbackService;

	@PostMapping
	public ResponseEntity<Void> createFeedback(@RequestBody @Valid FeedbackRequest request) {
		feedbackService.createFeedback(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	public ResponseEntity<List<FeedbackViewDto>> getAll() {
		return ResponseEntity.ok(feedbackService.fetchAll());
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		feedbackService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/companies/{companyId}")
	@PreAuthorize("hasAnyRole('COMPANY_ADMIN', 'COMPANY_USER')")
	public List<FeedbackViewDto> getFeedbacksByCompanyId(@PathVariable Long companyId) {
		return feedbackService.fetchAllByCompanyId(companyId);
	}


	@PutMapping("/{id}/assign-company")
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	public ResponseEntity<Void> assignCompany(@PathVariable Long id, @RequestBody @Valid AssignCompanyRequest request) {
		feedbackService.assignCompany(id, request);

		return ResponseEntity.ok().build();
	}
}
