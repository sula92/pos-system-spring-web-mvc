package com.pos.repository;

import com.pos.entity.OrderDetail;
import com.pos.entity.OrderDetailId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA Repository for OrderDetail
 * Provides CRUD operations and custom queries for OrderDetail entities.
 * Hibernate handles SQL generation automatically.
 */
// `@Repository` makes this interface a Spring bean.
// `JpaRepository<OrderDetail, OrderDetailId>` uses the composite key class as the ID type.
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    // JpaRepository already gives the basic CRUD methods.

    /**
     * Find all order details for a specific order ID
     * @param orderId The order ID to search for
     * @return List of order details for the given order
     */
    // JPQL query for fetching all detail rows for one order.
    @Query("SELECT od FROM OrderDetail od WHERE od.id.orderId = :orderId ORDER BY od.id.itemCode")
    List<OrderDetail> findByOrderId(@Param("orderId") String orderId);

    // `@EntityGraph` tells JPA to also load the parent `Order` when each detail is fetched.
    @EntityGraph(attributePaths = {"order"})
    @Query("SELECT od FROM OrderDetail od WHERE od.id.orderId = :orderId ORDER BY od.id.itemCode")
    List<OrderDetail> findByOrderIdWithOrderGraph(@Param("orderId") String orderId);

    // `JOIN FETCH` is the explicit join version of the same idea.
    @Query("SELECT od FROM OrderDetail od JOIN FETCH od.order WHERE od.id.orderId = :orderId ORDER BY od.id.itemCode")
    List<OrderDetail> findByOrderIdJoinFetchOrder(@Param("orderId") String orderId);

    // Native SQL version for cases where we want to inspect the exact table columns.
    @Query(value = "SELECT * FROM order_details WHERE order_id = :orderId ORDER BY item_code", nativeQuery = true)
    List<OrderDetail> findByOrderIdNative(@Param("orderId") String orderId);

    // Native aggregate query to total quantity by item code.
    @Query(value = "SELECT COALESCE(SUM(qty), 0) FROM order_details WHERE item_code = :itemCode", nativeQuery = true)
    int sumOrderedQtyByItemCodeNative(@Param("itemCode") String itemCode);
}


