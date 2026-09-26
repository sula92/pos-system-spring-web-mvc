package com.pos.entity;

import jakarta.persistence.*;

/**
 * Inventory Entity
 * Tracks stock levels for each item.
 */
@Entity
@Table(name = "inventory")
@NamedQueries({
        // JPQL query: list only items that are currently in stock.
        @NamedQuery(
                name = "Inventory.findInStock",
                query = "SELECT i FROM Inventory i WHERE i.qty > 0 ORDER BY i.itemCode"
        ),
        // JPQL query: find one inventory row by its item code.
        @NamedQuery(
                name = "Inventory.findByItemCode",
                query = "SELECT i FROM Inventory i WHERE i.itemCode = :itemCode"
        )
})
@NamedNativeQueries({
        // Native SQL version for exact table-column control.
        @NamedNativeQuery(
                name = "Inventory.findInStockNative",
                query = "SELECT * FROM inventory WHERE qty_on_hand > 0 ORDER BY item_code",
                resultClass = Inventory.class
        ),
        @NamedNativeQuery(
                name = "Inventory.findByItemCodeNative",
                query = "SELECT * FROM inventory WHERE item_code = :itemCode",
                resultClass = Inventory.class
        )
})
public class Inventory {

    // Primary key column; this also matches the foreign key to Item.
    @Id
    @Column(name = "item_code", length = 10)
    private String itemCode;

    // Quantity on hand in the database, mapped to the shorter Java field name `qty`.
    @Column(name = "qty_on_hand", nullable = false)
    private int qty;

    // `@MapsId` means this row shares the same primary key value as the linked Item.
    // This is the shared-primary-key pattern used for one-to-one stock records.
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_code")
    private Item item;

    public Inventory() {}

    public Inventory(String itemCode, int qty, Item item) {
        this.itemCode = itemCode;
        this.qty = qty;
        this.item = item;
    }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
}

