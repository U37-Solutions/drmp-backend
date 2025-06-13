package org.ua.drmp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.dto.ChangePasswordRequest;
import org.ua.drmp.dto.UserRequest;
import org.ua.drmp.dto.UserResponse;
import org.ua.drmp.entity.User;
import org.ua.drmp.service.UserService;
import org.ua.drmp.swagger.annotation.ApiError400;
import org.ua.drmp.swagger.annotation.ApiError403;
import org.ua.drmp.swagger.annotation.ApiError404;

@RestController
@RequiredArgsConstructor
public class UserController {
	public static final String USERS_ENDPOINT = "/users";
	private final UserService userService;

	@ApiError400
	@ApiError404
	@PostMapping(USERS_ENDPOINT + "/change-password")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
		userService.changePassword(changePasswordRequest);
		return ResponseEntity.ok("Password changed successfully");
	}

	@GetMapping(USERS_ENDPOINT)
	public List<UserResponse> fetchUsers() {
		return userService.fetchUsers();
	}

	@ApiError404
	@GetMapping(USERS_ENDPOINT + "/{id}")
	@PreAuthorize("@userSecurity.isAdminOrOwner(authentication, #id)")
	public UserResponse fetchUserById(@PathVariable("id") Long id) {
		return userService.fetchUserById(id);
	}

	@ApiError404
	@DeleteMapping(USERS_ENDPOINT + "/{id}")
	public ResponseEntity<?> deleteUserById(@PathVariable("id") Long id) {
		userService.deleteUserById(id);
		return ResponseEntity.ok("User deleted successfully");
	}

	@ApiError403
	@ApiError404
	@PutMapping(USERS_ENDPOINT + "/{id}")
	@PreAuthorize("@userSecurity.isOwner(authentication, #id)")
	public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody UserRequest userRequest) {
		userService.updateUser(id, userRequest);
		return ResponseEntity.ok("User updated successfully");
	}

	@ApiError404
	@GetMapping("/session-info")
	public User fetchUserInfo() {
		return userService.sessionInfo();
	}
}
