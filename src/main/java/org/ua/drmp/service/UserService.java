package org.ua.drmp.service;

import java.util.List;
import java.util.Optional;
import org.ua.drmp.dto.ChangePasswordRequest;
import org.ua.drmp.dto.UserRequest;
import org.ua.drmp.dto.UserResponse;
import org.ua.drmp.entity.User;

public interface UserService {
	void changePassword(ChangePasswordRequest changePasswordRequest);

	void resetPassword(String email, String newPassword);

	List<UserResponse> fetchUsers();

	User fetchByEmail(String email);

	UserResponse fetchUserById(Long id);

	void updateUser(Long id, UserRequest request);

	void deleteUserById(Long id);

	User sessionInfo();
}
