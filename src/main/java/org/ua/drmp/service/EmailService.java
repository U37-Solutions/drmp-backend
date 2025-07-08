package org.ua.drmp.service;

import org.ua.drmp.chat.entity.ChatMessage;

public interface EmailService {
	void sendResetPasswordEmail(String toEmail, String resetToken);

	void sendInviteUserEmail(String email, String token);

	void sendSuccessfulRegistrationEmail(String email);

	void notifyAdmin(ChatMessage message);

}
