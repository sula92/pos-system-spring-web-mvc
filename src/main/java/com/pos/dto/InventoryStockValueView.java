package com.pos.dto;

public record InventoryStockValueView(
        String itemCode,
        String description,
        double unitPrice,
        int qty,
        double inventoryValue
) {}

