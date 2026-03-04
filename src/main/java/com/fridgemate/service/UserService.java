package com.fridgemate.service;

import com.fridgemate.dto.auth.RegisterRequest;
import com.fridgemate.model.User;
import com.fridgemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // injected from SecurityConfig's @Bean

    // Handles new user registration.
    // Returns the saved User so the caller (AuthController) can generate a JWT for it.
    public User register(RegisterRequest request) {

        // Business rule: no two accounts can share an email address.
        // We check this before attempting to save — a cleaner error than a database constraint violation.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        // Build the User entity using the builder pattern Lombok generated.
        // Note: we hash the password here — the raw password from the request
        // is NEVER stored anywhere. passwordEncoder.encode() runs bcrypt.
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Persist to the database. save() returns the saved entity with the
        // database-generated id and createdAt fields now populated.
        return userRepository.save(user);
    }
}
