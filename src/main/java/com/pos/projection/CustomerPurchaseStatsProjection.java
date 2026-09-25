package com.pos.projection;

public interface CustomerPurchaseStatsProjection {
    String getCustomerId();
    String getCustomerName();
    String getEmail();
    Long getTotalOrders();
    Double getTotalSpent();
}

