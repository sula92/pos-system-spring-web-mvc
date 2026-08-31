package com.pos.repository;

import com.pos.entity.OrderDetailEntity;
import com.pos.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA Repository for OrderDetailEntity
 * Provides CRUD operations and custom queries for OrderDetail entities.
 * Hibernate handles SQL generation automatically.
 */
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, OrderDetailId> {
    // JpaRepository provides: save, update, delete, findById, findAll automatically

    /**
     * Find all order details for a specific order ID
     * @param orderId The order ID to search for
     * @return List of order details for the given order
     */
    @Query("SELECT od FROM OrderDetailEntity od WHERE od.id.orderId = :orderId ORDER BY od.id.itemCode")
    List<OrderDetailEntity> findByOrderId(@Param("orderId") String orderId);
}


