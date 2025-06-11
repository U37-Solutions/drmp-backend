package org.ua.drmp.service;

import java.util.List;
import org.ua.drmp.dto.ChangePasswordRequest;
import org.ua.drmp.dto.UserRequest;
import org.ua.drmp.dto.UserResponse;
import org.ua.drmp.entity.User;

public interface UserService {
	void changePassword(ChangePasswordRequest changePasswordRequest);

	List<UserResponse> fetchUsers();

	UserResponse fetchUserById(Long id);

	void updateUser(Long id, UserRequest request);

	void deleteUserById(Long id);

	User sessionInfo();
}
