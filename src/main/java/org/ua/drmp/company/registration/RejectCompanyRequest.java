package org.ua.drmp.company.registration;

public record RejectCompanyRequest(
	String message,
	Long assignedEditorId
) {}
