package org.ua.drmp.service.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ua.drmp.dto.InviteUserRequest;
import org.ua.drmp.exception.TokenValidationException;
import org.ua.drmp.service.InviteTokenService;

@Service
@RequiredArgsConstructor
public class InviteTokenServiceImpl implements InviteTokenService {

	private final Map<String, InviteUserRequest> tokenToUserDataMap = new ConcurrentHashMap<>();
	private final Map<String, Long> tokenTimestamps = new ConcurrentHashMap<>();

	private static final long EXPIRATION_MILLIS = 60 * 60 * 1000; // 1 година
	@Override
	public String createInviteToken(InviteUserRequest request) {
		String token = UUID.randomUUID().toString();
		tokenToUserDataMap.put(token, request);
		tokenTimestamps.put(token, System.currentTimeMillis());
		return token;
	}

	@Override
	public InviteUserRequest getUserDataByToken(String token) {
		Long createdAt = tokenTimestamps.get(token);
		if (createdAt == null || System.currentTimeMillis() - createdAt > EXPIRATION_MILLIS) {
			tokenToUserDataMap.remove(token);
			tokenTimestamps.remove(token);
			throw new TokenValidationException("Invite token expired or not found");
		}
		return tokenToUserDataMap.get(token);
	}

	@Override
	public void invalidateInviteToken(String token) {
		tokenToUserDataMap.remove(token);
		tokenTimestamps.remove(token);
	}
}
