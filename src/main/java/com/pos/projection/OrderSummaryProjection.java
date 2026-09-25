package com.pos.projection;

import java.time.LocalDate;

public interface OrderSummaryProjection {
    String getOrderId();
    LocalDate getOrderDate();
    String getCustomerId();
    Long getLineCount();
    Integer getTotalQty();
    Double getGrandTotal();
}

