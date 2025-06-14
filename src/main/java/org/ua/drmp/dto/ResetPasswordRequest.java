package org.ua.drmp.dto;

public record ResetPasswordRequest(String token, String password) {
}
