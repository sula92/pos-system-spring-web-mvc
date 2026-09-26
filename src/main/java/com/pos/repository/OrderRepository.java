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
// `@Repository` makes this interface a Spring bean.
// The generic type `<Order, String>` means the entity is `Order` and the primary key is `String`.
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    // JpaRepository already gives basic CRUD methods, so we only add custom reads here.

    // `@EntityGraph` tells JPA to load the `orderDetails` relationship with the order.
    // `EntityGraphType.LOAD` keeps the entity's normal fetch rules but adds this extra path.
    @EntityGraph(value = "Order.withDetails", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithDetailsGraph(@Param("orderId") String orderId);

    // This version uses `attributePaths` so we can define the graph directly in the repository.
    @EntityGraph(attributePaths = {"orderDetails"})
    @Query("SELECT o FROM Order o")
    List<Order> findAllWithDetailsGraph();

    // `JOIN FETCH` loads the parent order and its details in one query.
    // `DISTINCT` removes duplicate parent rows that can appear in a one-to-many join.
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.orderId = :orderId")
    Optional<Order> findByIdJoinFetch(@Param("orderId") String orderId);

    // Bulk read version of the same fetch strategy.
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails")
    List<Order> findAllJoinFetch();

    // Native SQL is useful when we want to inspect or debug the exact table structure.
    @Query(value = "SELECT * FROM orders WHERE order_id = :orderId", nativeQuery = true)
    Optional<Order> findByIdNative(@Param("orderId") String orderId);

    // Another native query, this time for a simple ordered list.
    @Query(value = "SELECT * FROM orders ORDER BY date DESC", nativeQuery = true)
    List<Order> findAllNativeOrderByDateDesc();

    // Native aggregate query: count orders for one customer directly in SQL.
    @Query(value = "SELECT COUNT(*) FROM orders WHERE customer_id = :customerId", nativeQuery = true)
    long countByCustomerIdNative(@Param("customerId") String customerId);

    // Interface-based projection: return only the summary columns, not full `Order` entities.
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

    // DTO constructor projection: JPA creates `OrderSummaryDTO` objects directly.
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


