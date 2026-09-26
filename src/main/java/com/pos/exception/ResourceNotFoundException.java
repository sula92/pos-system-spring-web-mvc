package com.pos.exception;

// Thrown when the requested record does not exist in the database.
// Extending RuntimeException means callers do not need to catch it explicitly.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        // Pass the error message to the base Exception class so it can be shown to the client.
        super(message);
    }
}

