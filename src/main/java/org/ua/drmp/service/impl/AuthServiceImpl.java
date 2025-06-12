package org.ua.drmp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		Role role = roleRepository.findByName(DRMPRole.USER)
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

		String accessToken = jwtUtils.generateAccessToken(userDetails);
		String refreshToken = jwtUtils.generateRefreshToken(userDetails);

		tokenRepository.saveAll(List.of(
			Token.builder().token(accessToken).user(user).expired(false).revoked(false).refreshToken(false).build(),
			Token.builder().token(refreshToken).user(user).expired(false).revoked(false).refreshToken(true).build()
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

		return Map.of("accessToken", newAccessToken,
			"accessTokenExpiresAt", accessTokenExpiry.toString());
	}

	@Override
	public void logout() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));

		List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
		if (!validTokens.isEmpty()) {
			validTokens.forEach(token -> {
				token.setRevoked(true);
				token.setExpired(true);
			});
			tokenRepository.saveAll(validTokens);
		}

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
}
