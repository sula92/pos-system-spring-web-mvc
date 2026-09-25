package com.pos.repository;

import com.pos.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Item
 * Provides CRUD operations and custom queries for Item entities.
 * Hibernate handles SQL generation automatically.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    // JpaRepository provides: save, update, delete, findById, findAll automatically
    // The generic parameter <Item, String> indicates String is the primary key type

    // Derived query examples (no @Query annotation required).
    List<Item> findByDescriptionContainingIgnoreCase(String keyword);

    List<Item> findByUnitPriceGreaterThanEqual(double minPrice);

    boolean existsByDescriptionIgnoreCase(String description);
}


