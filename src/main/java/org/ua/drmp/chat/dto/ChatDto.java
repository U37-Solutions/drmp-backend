package org.ua.drmp.chat.dto;

import java.time.Instant;

public record ChatDto(
	Long id,
	String accessToken,
	Instant createdAt,
	Instant expiresAt,
	Instant updatedAt,
	boolean archived
) {
}
