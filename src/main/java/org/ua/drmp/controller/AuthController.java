package org.ua.drmp.controller;

import java.util.Set;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.config.JwtUtils;
import org.ua.drmp.dto.AuthRequest;
import org.ua.drmp.entity.DRMPRole;
import org.ua.drmp.entity.Role;
import org.ua.drmp.entity.User;
import org.ua.drmp.repo.RoleRepository;
import org.ua.drmp.repo.UserRepository;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody AuthRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.badRequest().body("Email already in use");
		}

		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		Role role = roleRepository.findByName(DRMPRole.ROLE_USER)
			.orElseThrow(() -> new RuntimeException("Default role not found"));
		user.setRoles(Set.of(role));

		userRepository.save(user);
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
		Authentication auth = authManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		String token = jwtUtils.generateJwtToken((UserDetails) auth.getPrincipal());

		return ResponseEntity.ok(Map.of("token", token));
	}
}
