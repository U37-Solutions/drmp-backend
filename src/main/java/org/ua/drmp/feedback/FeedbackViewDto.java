package org.ua.drmp.feedback;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackViewDto {
	private Long id;
	private String email;
	private String name;
	private String message;
	private Long companyId;
	private String companyName;
}
