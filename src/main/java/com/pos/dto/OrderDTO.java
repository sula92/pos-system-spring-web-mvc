package com.pos.dto;

import java.time.LocalDate;
import java.util.List;

public class OrderDTO {
    private String orderId;
    private LocalDate date;
    private String customerId;
    private List<OrderDetailDTO> orderDetails;

    public OrderDTO() {}

    public OrderDTO(String orderId, LocalDate date, String customerId, List<OrderDetailDTO> orderDetails) {
        this.orderId = orderId;
        this.date = date;
        this.customerId = customerId;
        this.orderDetails = orderDetails;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public List<OrderDetailDTO> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetailDTO> orderDetails) { this.orderDetails = orderDetails; }
}

