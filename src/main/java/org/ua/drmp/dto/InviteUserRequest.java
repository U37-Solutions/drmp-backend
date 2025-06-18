package org.ua.drmp.dto;

public record InviteUserRequest(String email, String role, String firstName, String lastName) {
}
