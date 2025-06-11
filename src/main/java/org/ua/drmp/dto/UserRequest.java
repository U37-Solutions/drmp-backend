package org.ua.drmp.dto;

public record UserRequest(String email, String password, String firstName, String lastName) {
}
