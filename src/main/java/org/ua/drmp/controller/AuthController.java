package org.ua.drmp.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.dto.AuthRequest;
import org.ua.drmp.dto.RefreshRequest;
import org.ua.drmp.service.AuthService;
import org.ua.drmp.swagger.annotation.ApiError400;
import org.ua.drmp.swagger.annotation.ApiError401;
import org.ua.drmp.swagger.annotation.ApiError404;
import org.ua.drmp.swagger.annotation.ApiError409;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

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

}
