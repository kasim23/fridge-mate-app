package com.fridgemate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data               // Lombok: generates getters, setters, equals, hashCode, toString
@Builder            // Lombok: enables User.builder().email("...").build() pattern
@NoArgsConstructor  // Lombok: JPA requires a no-arg constructor
@AllArgsConstructor // Lombok: needed by @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL SERIAL / auto-increment
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true) // no two users can share an email
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password; // stored as a bcrypt hash, never plain text

    @CreationTimestamp
    @Column(nullable = false, updatable = false) // set once on insert, never changed
    private LocalDateTime createdAt;

    // One user owns many fridge items.
    // mappedBy = "user" tells Hibernate: the foreign key lives on the FridgeItem side.
    // CascadeType.ALL means if you delete a User, all their FridgeItems are deleted too.
    // orphanRemoval = true cleans up items that are removed from this list.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FridgeItem> fridgeItems = new ArrayList<>();
}
