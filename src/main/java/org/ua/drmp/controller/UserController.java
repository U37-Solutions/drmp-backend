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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.dto.ChangePasswordRequest;
import org.ua.drmp.dto.ConfirmRegistrationRequest;
import org.ua.drmp.dto.InviteCompanyUserRequest;
import org.ua.drmp.dto.InviteUserRequest;
import org.ua.drmp.dto.UserRequest;
import org.ua.drmp.dto.UserResponse;
import org.ua.drmp.dto.UserSessionResponse;
import org.ua.drmp.entity.DRMPRole;
import org.ua.drmp.service.UserService;
import org.ua.drmp.swagger.annotation.ApiError400;
import org.ua.drmp.swagger.annotation.ApiError401;
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

	// ADMIN, EDITOR
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	@GetMapping(USERS_ENDPOINT)
	public List<UserResponse> fetchUsers(@RequestParam(required = false) DRMPRole role) {
		return userService.fetchUsers(role);
	}

	// всі (ADMIN, EDITOR, власник (COMPANY_ADMIN, COMPANY_USER))
	@ApiError404
	@PreAuthorize("@userSecurity.isAdminOrOwnerOrEditor(authentication, #id)")
	@GetMapping(USERS_ENDPOINT + "/{id}")
	public UserResponse fetchUserById(@PathVariable("id") Long id) {
		return userService.fetchUserById(id);
	}

	@PreAuthorize("hasAnyRole('COMPANY_ADMIN', 'ADMIN', 'EDITOR')")
	@GetMapping(USERS_ENDPOINT + "/company/{companyId}")
	public List<UserResponse> fetchUserByCompanyId(@PathVariable("companyId") Long companyId) {
		return userService.fetchUsersByCompanyId(companyId);
	}


	// ADMIN, або власник (COMPANY_ADMIN або COMPANY_USER самого себе)
	@ApiError404
	@PreAuthorize("@userSecurity.isAdminOrOwner(authentication, #id)")
	@DeleteMapping(USERS_ENDPOINT + "/{id}")
	public ResponseEntity<?> deleteUserById(@PathVariable("id") Long id) {
		userService.deleteUserById(id);
		return ResponseEntity.ok("User deleted successfully");
	}

	// Власник (COMPANY_ADMIN або COMPANY_USER самого себе)
	@ApiError403
	@ApiError404
	@PreAuthorize("@userSecurity.isOwner(authentication, #id)")
	@PutMapping(USERS_ENDPOINT + "/{id}")
	public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody UserRequest userRequest) {
		userService.updateUser(id, userRequest);
		return ResponseEntity.ok("User updated successfully");
	}

	@ApiError404
	@GetMapping("/session-info")
	public UserSessionResponse fetchUserInfo() {
		return userService.sessionInfo();
	}

	@ApiError400
	@PostMapping("/invite")
	public ResponseEntity<?> inviteUser(@RequestBody InviteUserRequest request) {
		userService.inviteUser(request);
		return ResponseEntity.ok().build();
	}

	@ApiError400
	@ApiError401
	@GetMapping("/temporary/{token}")
	public ResponseEntity<InviteUserRequest> getTempUser(@PathVariable String token) {
		return ResponseEntity.ok(userService.getTemporaryUserData(token));
	}

	@ApiError400
	@ApiError401
	@ApiError404
	@PostMapping("/confirm-registration")
	public ResponseEntity<?> confirmRegistration(@RequestBody ConfirmRegistrationRequest request) {
		userService.confirmRegistration(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/invite-company-user")
	@PreAuthorize("hasAnyRole('COMPANY_ADMIN')")
	public ResponseEntity<?> inviteCompanyUser(@RequestBody InviteCompanyUserRequest request) {
		userService.inviteCompanyUser(request);
		return ResponseEntity.ok().build();
	}
}
