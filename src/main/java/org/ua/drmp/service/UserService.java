package org.ua.drmp.service;

import org.ua.drmp.dto.ChangePasswordRequest;

public interface UserService {
	void changePassword(ChangePasswordRequest changePasswordRequest);
}
