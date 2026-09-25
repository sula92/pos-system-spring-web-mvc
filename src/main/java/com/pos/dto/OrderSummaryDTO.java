package com.pos.dto;

import java.time.LocalDate;

public class OrderSummaryDTO {
    private final String orderId;
    private final LocalDate orderDate;
    private final String customerId;
    private final long lineCount;
    private final int totalQty;
    private final double grandTotal;

    public OrderSummaryDTO(
            String orderId,
            LocalDate orderDate,
            String customerId,
            long lineCount,
            int totalQty,
            double grandTotal
    ) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerId = customerId;
        this.lineCount = lineCount;
        this.totalQty = totalQty;
        this.grandTotal = grandTotal;
    }

    public String getOrderId() { return orderId; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getCustomerId() { return customerId; }
    public long getLineCount() { return lineCount; }
    public int getTotalQty() { return totalQty; }
    public double getGrandTotal() { return grandTotal; }
}

