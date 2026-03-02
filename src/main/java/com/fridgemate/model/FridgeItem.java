package com.fridgemate.model;

import com.fridgemate.model.enums.ItemCategory;
import com.fridgemate.model.enums.ItemLocation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fridge_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FridgeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key — this is the "owning" side of the relationship.
    // @JoinColumn tells Hibernate to create a user_id column in fridge_items.
    @ManyToOne(fetch = FetchType.LAZY) // LAZY = don't load the User object unless explicitly accessed
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING) // stores "DAIRY" not 0 — readable in the database
    @Column(nullable = false)
    private ItemCategory category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemLocation location;

    @Positive
    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private String unit; // "kg", "litres", "pieces", etc.

    @NotNull
    @Column(nullable = false)
    private LocalDate expiryDate;

    private LocalDate purchaseDate; // optional — user may not always know

    @Column(columnDefinition = "TEXT") // allows long text, maps to PostgreSQL TEXT type
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Hibernate automatically updates this on every save()
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
