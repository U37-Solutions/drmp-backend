package org.ua.drmp.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.dto.AuthRequest;
import org.ua.drmp.dto.ForgotPasswordRequest;
import org.ua.drmp.dto.RefreshRequest;
import org.ua.drmp.dto.ResetPasswordRequest;
import org.ua.drmp.service.AuthService;
import org.ua.drmp.service.EmailService;
import org.ua.drmp.service.PasswordResetTokenService;
import org.ua.drmp.service.UserService;
import org.ua.drmp.swagger.annotation.ApiError400;
import org.ua.drmp.swagger.annotation.ApiError401;
import org.ua.drmp.swagger.annotation.ApiError404;
import org.ua.drmp.swagger.annotation.ApiError409;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	private final EmailService emailService;
	private final PasswordResetTokenService tokenService;
	private final UserService userService;

	@ApiError401
	@ApiError404
	@ApiError409
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody AuthRequest request) {
		authService.register(request);
		return ResponseEntity.ok("User registered successfully");
	}

	@ApiError400
	@ApiError401
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@ApiError400
	@ApiError401
	@ApiError404
	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		authService.logout();
		return ResponseEntity.ok("Logged out successfully");
	}

	@ApiError401
	@ApiError404
	@PostMapping("/refresh")
	public ResponseEntity<Map<String, String>> refresh(@RequestBody RefreshRequest request) {
		return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
	}

	@ApiError400
	@ApiError404
	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		String email = request.email();

		// it's only for validation, in future (after we will add validation, remove that)
		userService.fetchByEmail(email);

		String token = tokenService.createResetToken(email);
		emailService.sendResetPasswordEmail(email, token);
		return ResponseEntity.ok().build();
	}

	@ApiError401
	@ApiError404
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
		String token = request.token();

		String email = tokenService.getEmailByResetToken(token);
		userService.resetPassword(email, request.password());
		tokenService.invalidateResetToken(token);
		return ResponseEntity.ok().build();
	}
}
