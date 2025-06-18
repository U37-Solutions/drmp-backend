package org.ua.drmp.service;

import org.ua.drmp.dto.InviteUserRequest;

public interface InviteTokenService {
	String createInviteToken(InviteUserRequest request);
	InviteUserRequest getUserDataByToken(String token);
	void invalidateInviteToken(String token);
}
