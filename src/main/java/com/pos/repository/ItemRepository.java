package com.pos.repository;

import com.pos.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for ItemEntity
 * Provides CRUD operations and custom queries for Item entities.
 * Hibernate handles SQL generation automatically.
 */
@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, String> {
    // JpaRepository provides: save, update, delete, findById, findAll automatically
    // The generic parameter <ItemEntity, String> indicates String is the primary key type
}


