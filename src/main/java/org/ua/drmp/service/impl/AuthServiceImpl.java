package org.ua.drmp.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.UUID;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.ua.drmp.exception.AuthorizationHeaderMissingException;
import org.ua.drmp.exception.BadRequestException;
import org.ua.drmp.exception.EmailAlreadyInUseException;
import org.ua.drmp.exception.ResourceNotFoundException;
import org.ua.drmp.exception.TokenValidationException;
import org.ua.drmp.exception.UserNotFoundException;
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
	private final HttpServletRequest request;

	@Override
	public void register(AuthRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyInUseException("Email already in use");
		}

		Role role = roleRepository.findByName(DRMPRole.USER)
			.orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

		User user = User.builder()
			.email(request.email())
			.password(passwordEncoder.encode(request.password()))
			.roles(Set.of(role))
			.build();

		userRepository.save(user);
	}

	@Override
	public Map<String, String> login(AuthRequest request) {
		try {
			Authentication auth = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));

			CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
			User user = userDetails.getUser();

			String accessToken = jwtUtils.generateAccessToken(userDetails);
			String refreshToken = jwtUtils.generateRefreshToken(userDetails);
			String sessionId = UUID.randomUUID().toString();

			tokenRepository.saveAll(List.of(
				Token.builder()
					.token(accessToken)
					.user(user)
					.expired(false)
					.revoked(false)
					.refreshToken(false)
					.sessionId(sessionId)
					.build(),

				Token.builder()
					.token(refreshToken)
					.user(user)
					.expired(false)
					.revoked(false)
					.refreshToken(true)
					.sessionId(sessionId)
					.build()
			));

			cleanUpTokens(user);

			Date accessTokenExpiry = jwtUtils.getExpirationDateFromToken(accessToken);
			Date refreshTokenExpiry = jwtUtils.getExpirationDateFromToken(refreshToken);

			return Map.of(
				"accessToken", accessToken,
				"accessTokenExpiresAt", accessTokenExpiry.toString(),
				"refreshToken", refreshToken,
				"refreshTokenExpiresAt", refreshTokenExpiry.toString()
			);
		} catch (Exception e) {
			throw new BadRequestException("Login failed");
		}
	}

	@Override
	public Map<String, String> refreshToken(String refreshToken) {
		if (!jwtUtils.validateJwtToken(refreshToken)) {
			throw new TokenValidationException("Invalid refresh token");
		}

		String email = jwtUtils.tryGetEmail(refreshToken)
			.orElseThrow(() -> new TokenValidationException("Email not found in token"));

		CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);
		User user = userDetails.getUser();

		Token storedRefreshToken = tokenRepository.findByToken(refreshToken)
			.orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

		if (storedRefreshToken.isExpired() || storedRefreshToken.isRevoked() || !storedRefreshToken.isRefreshToken()) {
			throw new TokenValidationException("Refresh token is not valid");
		}

		List<Token> validAccessTokens = tokenRepository.findAllValidAccessTokensByUser(user.getId());
		if (!validAccessTokens.isEmpty()) {
			validAccessTokens.forEach(token -> {
				token.setRevoked(true);
				token.setExpired(true);
			});
			tokenRepository.saveAll(validAccessTokens);
		}

		String newAccessToken = jwtUtils.generateAccessToken(userDetails);

		tokenRepository.save(Token.builder()
			.token(newAccessToken)
			.user(user)
			.expired(false)
			.revoked(false)
			.refreshToken(false)
			.build());

		cleanUpTokens(user);

		Date accessTokenExpiry = jwtUtils.getExpirationDateFromToken(newAccessToken);

		return Map.of(
			"accessToken", newAccessToken,
			"accessTokenExpiresAt", accessTokenExpiry.toString()
		);
	}


	@Override
	public void logout() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));

		String token = getCurrentToken();
		Token storedToken = tokenRepository.findByToken(token)
			.orElseThrow(() -> new ResourceNotFoundException("Token not found"));

		String sessionId = storedToken.getSessionId();
		List<Token> tokensFromSession = tokenRepository.findAllByUserAndSessionId(user.getId(), sessionId);

		tokensFromSession.forEach(t -> {
			t.setRevoked(true);
			t.setExpired(true);
		});
		tokenRepository.saveAll(tokensFromSession);

		cleanUpTokens(user);
	}



	/**
	 * Delete 'dead' tokens it's -> (expired/revoked) or 6 max active tokens.
	 */
	private void cleanUpTokens(User user) {
		List<Token> allTokens = tokenRepository.findAllByUserOrderByIdAsc(user.getId());

		List<Token> toRemove = allTokens.stream()
			.filter(token -> token.isExpired() || token.isRevoked())
			.toList();

		List<Token> activeTokens = allTokens.stream()
			.filter(token -> !token.isExpired() && !token.isRevoked())
			.toList();

		if (activeTokens.size() > 6) {
			int excess = activeTokens.size() - 6;
			List<Token> excessTokens = activeTokens.stream().limit(excess).toList();
			toRemove = new ArrayList<>(toRemove);
			toRemove.addAll(excessTokens);
		}

		if (!toRemove.isEmpty()) {
			tokenRepository.deleteAll(toRemove);
		}
	}

	private String getCurrentToken() {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		throw new AuthorizationHeaderMissingException("Authorization header is missing or invalid");
	}
}
