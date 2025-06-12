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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody AuthRequest request) {
		authService.register(request);
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		authService.logout();
		return ResponseEntity.ok("Logged out successfully");
	}

	@PostMapping("/refresh")
	public ResponseEntity<Map<String, String>> refresh(@RequestBody RefreshRequest request) {
		return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
	}

}
