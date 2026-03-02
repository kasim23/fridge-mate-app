package com.fridgemate.security;

import com.fridgemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok: generates a constructor for all final fields (our DI pattern)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security calls this method when it needs to load a user by their identifier.
    // Our identifier is email (Spring calls it "username" generically).
    // It returns a UserDetails object — Spring's abstraction for "an authenticated principal".
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        // Our User entity will implement UserDetails in the next step —
        // that's how it can be returned directly here.
    }
}
