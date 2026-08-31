package com.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity
 * Maps to 'orders' table in PostgreSQL database.
 * This entity represents a sales order in the POS system.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "order_id", length = 10)
    private String orderId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "customer_id", length = 10, nullable = false)
    private String customerId;

    // Relationship to Order Details
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetailEntity> orderDetails = new ArrayList<>();

    public OrderEntity() {}

    public OrderEntity(String orderId, LocalDate date, String customerId) {
        this.orderId = orderId;
        this.date = date;
        this.customerId = customerId;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public List<OrderDetailEntity> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetailEntity> orderDetails) { this.orderDetails = orderDetails; }
}

