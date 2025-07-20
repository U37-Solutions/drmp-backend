package org.ua.drmp.feedback;

import jakarta.validation.constraints.NotBlank;

public record FeedbackRequest(
	String email,
	String name,
	@NotBlank String message
) {}