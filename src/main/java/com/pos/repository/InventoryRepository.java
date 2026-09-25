package com.pos.repository;

import com.pos.dto.InventoryStockValueView;
import com.pos.entity.Inventory;
import com.pos.projection.InventoryStockViewProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    // Derived query examples (no @Query annotation required).
    List<Inventory> findByQtyGreaterThanOrderByQtyDesc(int minQty);

    List<Inventory> findByQtyLessThanEqualOrderByQtyAsc(int maxQty);

    @Query("SELECT i FROM Inventory i ORDER BY i.itemCode")
    List<Inventory> findAllOrderByItemCode();

    //pessimistic lock is used to prevent concurrent updates. it locks the row during the transaction where as optimistic lock uses versioning.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.itemCode = :itemCode")
    Optional<Inventory> findByItemCodeForUpdate(@Param("itemCode") String itemCode);

    @Query(value = "SELECT * FROM inventory WHERE qty_on_hand > 0 ORDER BY item_code", nativeQuery = true)
    List<Inventory> findInStockNative();

    // Projection query for stock dashboards with item info and inventory value calculation.
    @Query("SELECT inv.itemCode AS itemCode, " +
            "it.description AS description, " +
            "it.unitPrice AS unitPrice, " +
            "inv.qty AS qty, " +
            "(inv.qty * it.unitPrice) AS inventoryValue " +
            "FROM Inventory inv JOIN inv.item it " +
            "ORDER BY it.description")
    List<InventoryStockViewProjection> findInventoryStockViews();

    // Record projection alternative for a stable API/report output format.
    @Query("SELECT new com.pos.dto.InventoryStockValueView(" +
            "inv.itemCode, " +
            "it.description, " +
            "it.unitPrice, " +
            "inv.qty, " +
            "(inv.qty * it.unitPrice)) " +
            "FROM Inventory inv JOIN inv.item it " +
            "ORDER BY it.description")
    List<InventoryStockValueView> findInventoryStockValueViews();
}

