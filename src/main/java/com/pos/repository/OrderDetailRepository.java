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
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    // JpaRepository provides: save, update, delete, findById, findAll automatically

    /**
     * Find all order details for a specific order ID
     * @param orderId The order ID to search for
     * @return List of order details for the given order
     */
    @Query("SELECT od FROM OrderDetail od WHERE od.id.orderId = :orderId ORDER BY od.id.itemCode")
    List<OrderDetail> findByOrderId(@Param("orderId") String orderId);

    // Graph-based fetch to force loading the parent order with each detail.
    @EntityGraph(attributePaths = {"order"})
    @Query("SELECT od FROM OrderDetail od WHERE od.id.orderId = :orderId ORDER BY od.id.itemCode")
    List<OrderDetail> findByOrderIdWithOrderGraph(@Param("orderId") String orderId);

    // JOIN FETCH variant for single-query retrieval of details and their parent order.
    @Query("SELECT od FROM OrderDetail od JOIN FETCH od.order WHERE od.id.orderId = :orderId ORDER BY od.id.itemCode")
    List<OrderDetail> findByOrderIdJoinFetchOrder(@Param("orderId") String orderId);

    // Native SQL equivalent of findByOrderId for direct table-level troubleshooting.
    @Query(value = "SELECT * FROM order_details WHERE order_id = :orderId ORDER BY item_code", nativeQuery = true)
    List<OrderDetail> findByOrderIdNative(@Param("orderId") String orderId);

    // Native aggregate query for simple sales analytics per item.
    @Query(value = "SELECT COALESCE(SUM(qty), 0) FROM order_details WHERE item_code = :itemCode", nativeQuery = true)
    int sumOrderedQtyByItemCodeNative(@Param("itemCode") String itemCode);
}


