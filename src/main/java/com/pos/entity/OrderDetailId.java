package com.pos.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for `OrderDetail`.
 *
 * `order_id + item_code` together identify one row, because one order can contain
 * the same item only once in this design.
 */
// `@Embeddable` means this ID class can be embedded inside another entity.
@Embeddable
public class OrderDetailId implements Serializable {

    // The order this line item belongs to.
    private String orderId;
    // The item being sold in this line item.
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
        // Composite keys must compare both fields so Hibernate can match rows correctly.
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailId that = (OrderDetailId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(itemCode, that.itemCode);
    }

    @Override
    public int hashCode() {
        // hashCode must use the same fields as equals.
        return Objects.hash(orderId, itemCode);
    }
}

