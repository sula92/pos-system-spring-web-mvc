package com.pos.repository;

import com.pos.dto.OrderSummaryDTO;
import com.pos.entity.Order;
import com.pos.projection.OrderSummaryProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Order
 * Provides CRUD operations and custom queries for Order entities.
 * Hibernate handles SQL generation automatically.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    // JpaRepository provides: save, update, delete, findById, findAll automatically
    // The generic parameter <Order, String> indicates String is the primary key type

    // Uses a named entity graph to fetch orderDetails with the order in one query.
    @EntityGraph(value = "Order.withDetails", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithDetailsGraph(@Param("orderId") String orderId);

    // Attribute-path graph variant for fetching detail collections when loading all orders.
    @EntityGraph(attributePaths = {"orderDetails"})
    @Query("SELECT o FROM Order o")
    List<Order> findAllWithDetailsGraph();

    //when you want to fetch order details with the order in one query.
    // JOIN FETCH removes N+1 selects by loading orders and their details in one query.
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.orderId = :orderId")
    Optional<Order> findByIdJoinFetch(@Param("orderId") String orderId);

    //when you want to fetch order details with the order in one query.
    // JOIN FETCH for bulk reads; DISTINCT avoids duplicate parent rows in the result list.
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails")
    List<Order> findAllJoinFetch();

    // Native SQL by primary key; useful when you need vendor-specific SQL debugging.
    @Query(value = "SELECT * FROM orders WHERE order_id = :orderId", nativeQuery = true)
    Optional<Order> findByIdNative(@Param("orderId") String orderId);

    // Native SQL ordered read for reporting-style screens.
    @Query(value = "SELECT * FROM orders ORDER BY date DESC", nativeQuery = true)
    List<Order> findAllNativeOrderByDateDesc();

    // Native aggregate query to quickly count orders for a customer.
    @Query(value = "SELECT COUNT(*) FROM orders WHERE customer_id = :customerId", nativeQuery = true)
    long countByCustomerIdNative(@Param("customerId") String customerId);

    // Projection query that returns a report-style shape instead of Order entities.
    @Query("SELECT o.orderId AS orderId, " +
            "o.date AS orderDate, " +
            "o.customerId AS customerId, " +
            "COUNT(od) AS lineCount, " +
            "COALESCE(SUM(od.qty), 0) AS totalQty, " +
            "COALESCE(SUM(od.qty * od.unitPrice), 0) AS grandTotal " +
            "FROM Order o LEFT JOIN o.orderDetails od " +
            "GROUP BY o.orderId, o.date, o.customerId " +
            "ORDER BY o.date DESC")
    List<OrderSummaryProjection> findOrderSummaries();

    // Constructor DTO projection alternative to interface-based projection.
    @Query("SELECT new com.pos.dto.OrderSummaryDTO(" +
            "o.orderId, " +
            "o.date, " +
            "o.customerId, " +
            "COUNT(od), " +
            "COALESCE(SUM(od.qty), 0), " +
            "COALESCE(SUM(od.qty * od.unitPrice), 0.0)) " +
            "FROM Order o LEFT JOIN o.orderDetails od " +
            "GROUP BY o.orderId, o.date, o.customerId " +
            "ORDER BY o.date DESC")
    List<OrderSummaryDTO> findOrderSummariesDto();
}


