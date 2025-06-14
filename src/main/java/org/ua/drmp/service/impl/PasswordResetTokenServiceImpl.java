package org.ua.drmp.service.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ua.drmp.exception.TokenValidationException;
import org.ua.drmp.service.PasswordResetTokenService;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
	private final Map<String, String> tokenToEmailMap = new ConcurrentHashMap<>();
	private final Map<String, Long> tokenTimestamps = new ConcurrentHashMap<>();

	private static final long EXPIRATION_MILLIS = 15 * 60 * 1000; // 15 хвилин

	@Override
	public String createResetToken(String email) {
		String token = UUID.randomUUID().toString();
		tokenToEmailMap.put(token, email);
		tokenTimestamps.put(token, System.currentTimeMillis());
		return token;
	}

	@Override
	public String getEmailByResetToken(String token) {
		Long createdAt = tokenTimestamps.get(token);
		if (createdAt == null || System.currentTimeMillis() - createdAt > EXPIRATION_MILLIS) {
			tokenToEmailMap.remove(token);
			tokenTimestamps.remove(token);
			throw new TokenValidationException("Reset token expired or not found");
		}
		return tokenToEmailMap.get(token);
	}

	@Override
	public void invalidateResetToken(String token) {
		tokenToEmailMap.remove(token);
		tokenTimestamps.remove(token);
	}
}
