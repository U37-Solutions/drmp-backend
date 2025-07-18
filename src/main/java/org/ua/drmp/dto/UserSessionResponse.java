package org.ua.drmp.dto;

import org.ua.drmp.entity.DRMPRole;

public record UserSessionResponse(long id, String email, String firstName, String lastName, DRMPRole role, Long companyId) {
}
