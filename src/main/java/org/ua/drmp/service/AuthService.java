package org.ua.drmp.service;

import java.util.Map;
import org.ua.drmp.dto.AuthRequest;

public interface AuthService {
	void register(AuthRequest request);
	Map<String, String> login(AuthRequest request);

	Map<String, String> refreshToken(String refreshToken);
	void logout(Long userId);
}
