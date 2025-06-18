package org.ua.drmp.service;

public interface EmailService {
	void sendResetPasswordEmail(String toEmail, String resetToken);

	void sendInviteUserEmail(String email, String token);

	void sendSuccessfulRegistrationEmail(String email);
}
