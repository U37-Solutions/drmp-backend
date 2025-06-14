package org.ua.drmp.service;

public interface EmailService {
	void sendResetPasswordEmail(String toEmail, String resetToken);
}
