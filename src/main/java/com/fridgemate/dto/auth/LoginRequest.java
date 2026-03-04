package com.fridgemate.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// What the client sends when logging in.
// Simpler than RegisterRequest — just credentials, no name or size constraints.
@Data
public class LoginRequest {

    @Email(message = "Must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
