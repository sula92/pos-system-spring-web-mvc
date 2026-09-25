package com.pos.repository;

import com.pos.dto.CustomerPurchaseStatsDTO;
import com.pos.entity.Customer;
import com.pos.projection.CustomerPurchaseStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Customer
 * Provides CRUD operations and custom queries for Customer entities.
 * Hibernate handles SQL generation automatically.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    // JpaRepository provides: save, update, delete, findById, findAll automatically
    // Additional custom queries can be added here using @Query annotations if needed

    // Derived query examples (no @Query annotation required).
    List<Customer> findByNameContainingIgnoreCase(String namePart);

    Optional<Customer> findByEmailIgnoreCase(String email);

    // Native projection for customer-level order and spending metrics.
    @Query(value = "SELECT c.id AS customerId, " +
            "c.name AS customerName, " +
            "c.email AS email, " +
            "COUNT(DISTINCT o.order_id) AS totalOrders, " +
            "COALESCE(SUM(od.qty * od.unit_price), 0) AS totalSpent " +
            "FROM customers c " +
            "LEFT JOIN orders o ON o.customer_id = c.id " +
            "LEFT JOIN order_details od ON od.order_id = o.order_id " +
            "GROUP BY c.id, c.name, c.email " +
            "ORDER BY c.name", nativeQuery = true)
    List<CustomerPurchaseStatsProjection> findCustomerPurchaseStats();

    // DTO constructor projection alternative with JPQL.
    @Query("SELECT new com.pos.dto.CustomerPurchaseStatsDTO(" +
            "c.id, " +
            "c.name, " +
            "c.email, " +
            "COUNT(DISTINCT o.orderId), " +
            "COALESCE(SUM(od.qty * od.unitPrice), 0.0)) " +
            "FROM Customer c " +
            "LEFT JOIN Order o ON o.customerId = c.id " +
            "LEFT JOIN o.orderDetails od " +
            "GROUP BY c.id, c.name, c.email " +
            "ORDER BY c.name")
    List<CustomerPurchaseStatsDTO> findCustomerPurchaseStatsDto();
}


