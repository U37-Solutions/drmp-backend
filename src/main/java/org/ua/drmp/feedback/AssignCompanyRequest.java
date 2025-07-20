package org.ua.drmp.feedback;

import jakarta.validation.constraints.NotNull;

public record AssignCompanyRequest(
	@NotNull Long companyId
) {
}
