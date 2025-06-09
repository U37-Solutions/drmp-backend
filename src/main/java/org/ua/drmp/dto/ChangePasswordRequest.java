package org.ua.drmp.dto;

public record ChangePasswordRequest (String oldPassword, String newPassword){}
