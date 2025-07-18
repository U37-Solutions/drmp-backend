package org.ua.drmp.service;

import org.ua.drmp.chat.entity.ChatMessage;
import org.ua.drmp.company.entity.Company;

public interface EmailService {
	void sendResetPasswordEmail(String toEmail, String resetToken);

	void sendInviteUserEmail(String email, String token);

	void sendSuccessfulRegistrationEmail(String email);

	void notifyAdmin(ChatMessage message);

	void sendCompanyRegistrationNotification(Company company);

	void sendAccountCredentialsEmail(String to, String rawPassword);

	void sendCompanyRejectionEmail(String to, String companyName, String reason, String contactEmail);
}
