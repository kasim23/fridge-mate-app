package com.fridgemate.repository;

import com.fridgemate.model.FridgeItem;
import com.fridgemate.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FridgeItemRepository extends JpaRepository<FridgeItem, Long> {

    // Derived query — fetches all items belonging to a specific user
    // SELECT * FROM fridge_items WHERE user_id = ?
    List<FridgeItem> findByUser(User user);

    // Custom JPQL query — finds items expiring on or before a given date,
    // ordered soonest-expiring first. Used by the scheduler and the expiring endpoint.
    // Note: JPQL uses class name "FridgeItem" and field name "expiryDate", not the table/column names.
    @Query("SELECT f FROM FridgeItem f WHERE f.user = :user AND f.expiryDate <= :date ORDER BY f.expiryDate ASC")
    List<FridgeItem> findExpiringSoon(@Param("user") User user, @Param("date") LocalDate date);

    // Derived query — used by the scheduler to process all users' items in one pass.
    // Finds every item (across all users) expiring within a date range.
    // SELECT * FROM fridge_items WHERE expiry_date BETWEEN ? AND ?
    List<FridgeItem> findByExpiryDateBetween(LocalDate from, LocalDate to);
}
