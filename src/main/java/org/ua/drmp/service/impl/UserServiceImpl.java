package org.ua.drmp.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ua.drmp.dto.ChangePasswordRequest;
import org.ua.drmp.dto.UserRequest;
import org.ua.drmp.dto.UserResponse;
import org.ua.drmp.entity.User;
import org.ua.drmp.exception.ForbiddenOperationException;
import org.ua.drmp.exception.InvalidPasswordException;
import org.ua.drmp.exception.UserNotFoundException;
import org.ua.drmp.repo.TokenRepository;
import org.ua.drmp.repo.UserRepository;
import org.ua.drmp.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenRepository tokenRepository;

	@Override
	public void changePassword(ChangePasswordRequest changePasswordRequest) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));

		if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
			throw new InvalidPasswordException("Old password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
		userRepository.save(user);
	}

	@Override
	public void resetPassword(String email, String newPassword) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	@Override
	public List<UserResponse> fetchUsers() {
		return userRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	public User fetchByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	@Override
	public UserResponse fetchUserById(Long id) {
		return mapToResponse(userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException("User not found")));
	}

	@Override
	public void updateUser(Long id, UserRequest request) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException("User not found"));

		String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!user.getEmail().equals(currentEmail)) {
			throw new ForbiddenOperationException("You are not allowed to update this user");
		}

		if (request.email() != null) {
			user.setEmail(request.email());
		}
		if (request.password() != null) {
			user.setPassword(passwordEncoder.encode(request.password()));
		}
		if (request.firstName() != null) {
			user.setFirstName(request.firstName());
		}
		if (request.lastName() != null) {
			user.setLastName(request.lastName());
		}
		userRepository.save(user);
	}

	@Override
	public void deleteUserById(Long id) {
		if (!userRepository.existsById(id)) {
			throw new UserNotFoundException("User not found");
		}
		tokenRepository.deleteAll(tokenRepository.findAllValidTokensByUser(id));
		userRepository.deleteById(id);
	}

	@Override
	public User sessionInfo() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	private UserResponse mapToResponse(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
	}
}
