package com.pos.exception;

// Thrown when there is not enough inventory to complete an order.
// The service layer uses this to stop the transaction before stock goes below zero.
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        // Save the detailed stock message for the API error response.
        super(message);
    }
}

