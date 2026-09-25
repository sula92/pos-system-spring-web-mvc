package com.pos.dto;

public class CustomerPurchaseStatsDTO {
    private final String customerId;
    private final String customerName;
    private final String email;
    private final long totalOrders;
    private final double totalSpent;

    public CustomerPurchaseStatsDTO(
            String customerId,
            String customerName,
            String email,
            long totalOrders,
            double totalSpent
    ) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
    }

    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public long getTotalOrders() { return totalOrders; }
    public double getTotalSpent() { return totalSpent; }
}

