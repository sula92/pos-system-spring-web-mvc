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

// `@Repository` marks this interface as a Spring Data repository bean.
// `JpaRepository<Inventory, String>` means this repository works with `Inventory`
// entities and the primary key type is `String`.
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    // Spring Data reads the method name and builds the query automatically.
    List<Inventory> findByQtyGreaterThanOrderByQtyDesc(int minQty);

    List<Inventory> findByQtyLessThanEqualOrderByQtyAsc(int maxQty);

    // `@Query` lets us write the JPQL directly when the method name would be too long.
    @Query("SELECT i FROM Inventory i ORDER BY i.itemCode")
    List<Inventory> findAllOrderByItemCode();

    // `@Lock(PESSIMISTIC_WRITE)` blocks other transactions from changing the same row
    // until this transaction finishes, which helps prevent stock race conditions.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.itemCode = :itemCode")
    Optional<Inventory> findByItemCodeForUpdate(@Param("itemCode") String itemCode);

    // Native SQL is used here because the query works directly on the database table.
    @Query(value = "SELECT * FROM inventory WHERE qty_on_hand > 0 ORDER BY item_code", nativeQuery = true)
    List<Inventory> findInStockNative();

    // Interface projection: only the selected columns are returned, not the full entity.
    @Query("SELECT inv.itemCode AS itemCode, " +
            "it.description AS description, " +
            "it.unitPrice AS unitPrice, " +
            "inv.qty AS qty, " +
            "(inv.qty * it.unitPrice) AS inventoryValue " +
            "FROM Inventory inv JOIN inv.item it " +
            "ORDER BY it.description")
    List<InventoryStockViewProjection> findInventoryStockViews();

    // DTO version of the same dashboard data.
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

