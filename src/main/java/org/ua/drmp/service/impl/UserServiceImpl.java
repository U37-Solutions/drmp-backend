package org.ua.drmp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ua.drmp.dto.ChangePasswordRequest;
import org.ua.drmp.entity.User;
import org.ua.drmp.repo.UserRepository;
import org.ua.drmp.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void changePassword(ChangePasswordRequest changePasswordRequest) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
			throw new RuntimeException("Old password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
		userRepository.save(user);
	}
}
