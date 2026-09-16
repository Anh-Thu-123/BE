package com.nagare.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank @Size(min = 4, max = 32) String username,
            @NotBlank @Size(min = 6, max = 64) String password) {}

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record RefreshRequest(@NotBlank String refreshToken) {}

    public record ChangePasswordRequest(@NotBlank String currentPassword,
                                         @NotBlank @Size(min = 6, max = 64) String newPassword) {}

    public record TokenResponse(String accessToken, String refreshToken, long expiresIn) {}

    public record MeResponse(String id, String username, String role, boolean mustChangePassword,
                              String employeeId, String customerId) {}
}
