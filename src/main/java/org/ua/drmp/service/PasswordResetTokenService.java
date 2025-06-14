package org.ua.drmp.service;

public interface PasswordResetTokenService {
	String createResetToken(String email);
	String getEmailByResetToken(String token);
	void invalidateResetToken(String token);
}
