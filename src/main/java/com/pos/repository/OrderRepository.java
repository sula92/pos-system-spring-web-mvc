package com.pos.repository;

import com.pos.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for OrderEntity
 * Provides CRUD operations and custom queries for Order entities.
 * Hibernate handles SQL generation automatically.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    // JpaRepository provides: save, update, delete, findById, findAll automatically
    // The generic parameter <OrderEntity, String> indicates String is the primary key type
}


