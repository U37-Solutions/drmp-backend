package org.ua.drmp.chat.dto;

import java.time.Instant;

public record ChatResponse(Long chatId, String accessToken, Instant expiresAt) {
}
