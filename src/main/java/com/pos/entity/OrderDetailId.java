package com.pos.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Primary Key for OrderDetailEntity
 * Represents the combination of order_id and item_code as a single primary key.
 */
@Embeddable
public class OrderDetailId implements Serializable {

    private String orderId;
    private String itemCode;

    public OrderDetailId() {}

    public OrderDetailId(String orderId, String itemCode) {
        this.orderId = orderId;
        this.itemCode = itemCode;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailId that = (OrderDetailId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(itemCode, that.itemCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, itemCode);
    }
}

