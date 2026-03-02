package com.fridgemate.repository;

import com.fridgemate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Derived query — Spring generates: SELECT * FROM users WHERE email = ?
    // Returns Optional<User> so callers are forced to handle the "not found" case explicitly
    Optional<User> findByEmail(String email);

    // Derived query — SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)
    // Used during registration to check if an email is already taken
    boolean existsByEmail(String email);
}
