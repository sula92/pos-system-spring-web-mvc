package com.pos.entity;

import jakarta.persistence.*;

/**
 * Item Entity
 * Maps to 'items' table in PostgreSQL database.
 * This entity represents a product/item in the inventory.
 */
@Entity
@Table(name = "items")
public class ItemEntity {

    @Id
    @Column(name = "code", length = 10)
    private String code;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "qty_on_hand", nullable = false)
    private int qtyOnHAnd;

    public ItemEntity() {}

    public ItemEntity(String code, String description, double unitPrice, int qtyOnHAnd) {
        this.code = code;
        this.description = description;
        this.unitPrice = unitPrice;
        this.qtyOnHAnd = qtyOnHAnd;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQtyOnHAnd() { return qtyOnHAnd; }
    public void setQtyOnHAnd(int qtyOnHAnd) { this.qtyOnHAnd = qtyOnHAnd; }
}
