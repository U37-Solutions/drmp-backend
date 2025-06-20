package org.ua.drmp.service;

import java.util.List;
import org.ua.drmp.dto.ChangePasswordRequest;
import org.ua.drmp.dto.ConfirmRegistrationRequest;
import org.ua.drmp.dto.InviteUserRequest;
import org.ua.drmp.dto.UserRequest;
import org.ua.drmp.dto.UserResponse;
import org.ua.drmp.dto.UserSessionResponse;
import org.ua.drmp.entity.User;

public interface UserService {
	void changePassword(ChangePasswordRequest changePasswordRequest);

	void resetPassword(String email, String newPassword);

	List<UserResponse> fetchUsers();

	User fetchByEmail(String email);

	UserResponse fetchUserById(Long id);

	void updateUser(Long id, UserRequest request);

	void deleteUserById(Long id);

	UserSessionResponse sessionInfo();

	void inviteUser(InviteUserRequest request);

	InviteUserRequest getTemporaryUserData(String token);

	void confirmRegistration(ConfirmRegistrationRequest request);
}
