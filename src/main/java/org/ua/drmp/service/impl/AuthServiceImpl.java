package org.ua.drmp.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ua.drmp.config.CustomUserDetails;
import org.ua.drmp.config.CustomUserDetailsService;
import org.ua.drmp.config.JwtUtils;
import org.ua.drmp.dto.AuthRequest;
import org.ua.drmp.entity.DRMPRole;
import org.ua.drmp.entity.Role;
import org.ua.drmp.entity.Token;
import org.ua.drmp.entity.User;
import org.ua.drmp.repo.RoleRepository;
import org.ua.drmp.repo.TokenRepository;
import org.ua.drmp.repo.UserRepository;
import org.ua.drmp.service.AuthService;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authManager;
	private final JwtUtils jwtUtils;
	private final CustomUserDetailsService customUserDetailsService;
	@Override
	public void register(AuthRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new RuntimeException("Email already in use");
		}

		Role role = roleRepository.findByName(DRMPRole.ROLE_USER)
			.orElseThrow(() -> new RuntimeException("Default role not found"));

		User user = User.builder()
			.email(request.email())
			.password(passwordEncoder.encode(request.password()))
			.roles(Set.of(role))
			.build();

		userRepository.save(user);
	}

	@Override
	public Map<String, String> login(AuthRequest request) {
		Authentication auth = authManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
		User user = userDetails.getUser();

		logout(user.getId());

		String accessToken = jwtUtils.generateAccessToken(userDetails);
		String refreshToken = jwtUtils.generateRefreshToken(userDetails);

		tokenRepository.saveAll(List.of(
			Token.builder().token(accessToken).user(user).expired(false).revoked(false).refreshToken(false).build(),
			Token.builder().token(refreshToken).user(user).expired(false).revoked(false).refreshToken(true).build()
		));

		return Map.of(
			"accessToken", accessToken,
			"refreshToken", refreshToken
		);
	}

	@Override
	public Map<String, String> refreshToken(String refreshToken) {
		if (!jwtUtils.validateJwtToken(refreshToken)) {
			throw new RuntimeException("Invalid refresh token");
		}

		String email = jwtUtils.getEmailFromJwtToken(refreshToken);

		CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);
		User user = userDetails.getUser();

		Token storedRefreshToken = tokenRepository.findByToken(refreshToken)
			.orElseThrow(() -> new RuntimeException("Refresh token not found"));

		if (storedRefreshToken.isExpired() || storedRefreshToken.isRevoked() || !storedRefreshToken.isRefreshToken()) {
			throw new RuntimeException("Refresh token is not valid");
		}

		// Деактивуємо всі активні access токени (не refresh) цього користувача
		List<Token> validAccessTokens = tokenRepository.findAllValidAccessTokensByUser(user.getId());
		if (!validAccessTokens.isEmpty()) {
			validAccessTokens.forEach(token -> {
				token.setRevoked(true);
				token.setExpired(true);
			});
			tokenRepository.saveAll(validAccessTokens);
		}

		// Генеруємо новий access токен
		String newAccessToken = jwtUtils.generateAccessToken(userDetails);

		// Зберігаємо новий access токен
		tokenRepository.save(Token.builder()
			.token(newAccessToken)
			.user(user)
			.expired(false)
			.revoked(false)
			.refreshToken(false)
			.build());

		return Map.of("accessToken", newAccessToken);
	}


	@Override
	public void logout(Long userId) {
		List<Token> validTokens = tokenRepository.findAllValidTokensByUser(userId);
		if (!validTokens.isEmpty()) {
			validTokens.forEach(token -> {
				token.setRevoked(true);
				token.setExpired(true);
			});
			tokenRepository.saveAll(validTokens);
		}
	}
}
