package org.ua.drmp.dto;

public record ConfirmRegistrationRequest(String token, String firstName, String lastName, String password) {
}
