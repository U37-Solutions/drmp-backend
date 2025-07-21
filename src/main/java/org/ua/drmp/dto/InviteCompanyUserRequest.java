package org.ua.drmp.dto;

public record InviteCompanyUserRequest(String email, String password, String firstName, String lastName) {
}
