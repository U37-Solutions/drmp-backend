package org.ua.drmp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.ua.drmp.repo.UserRepository;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

	private final UserRepository userRepository;

	public boolean isOwner(Authentication authentication, Long userId) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
			.map(user -> user.getId().equals(userId))
			.orElse(false);
	}

	public boolean isAdminOrOwner(Authentication authentication, Long userId) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
			.map(user -> user.getId().equals(userId) || user.hasRole("ADMIN"))
			.orElse(false);
	}
}
