package com.fridgemate.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// This is what the client sends in the request body when registering.
// It is a plain Java class — no @Entity, no database involvement.
// Its only job is to carry data from the HTTP request into our application.
@Data // Lombok: generates getters + setters — needed so Spring can deserialise JSON into this object
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    // @Email checks the string matches an email pattern (contains @, has a domain, etc.)
    @Email(message = "Must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    // @Size enforces minimum length — we don't want "abc" as a password
    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is required")
    private String password;
}
