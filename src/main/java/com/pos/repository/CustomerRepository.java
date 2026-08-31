package com.pos.repository;

import com.pos.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for CustomerEntity
 * Provides CRUD operations and custom queries for Customer entities.
 * Hibernate handles SQL generation automatically.
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
    // JpaRepository provides: save, update, delete, findById, findAll automatically
    // Additional custom queries can be added here using @Query annotations if needed
}


