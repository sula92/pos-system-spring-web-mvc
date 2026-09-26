package com.pos.exception.handler;

import com.pos.exception.InsufficientStockException;
import com.pos.exception.InvalidRequestException;
import com.pos.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

// @ControllerAdvice tells Spring that this class should watch all controllers
// and handle their exceptions in one central place.
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    // @ExceptionHandler links this method to a specific exception type.
    // When that exception is thrown, Spring calls this method automatically.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        // 404 means the requested record or resource could not be found.
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // One handler can listen for more than one exception type.
    @ExceptionHandler({InvalidRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException ex) {
        // These errors mean the client sent bad input, so return 400 Bad Request.
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // This maps a business-rule problem to a 409 Conflict response.
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(InsufficientStockException ex) {
        // 409 Conflict is a good fit when the request cannot be completed because stock is too low.
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Catch any exception that was not handled above.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        // This is the fallback for anything we did not expect or handle separately.
        logger.severe("Unhandled exception: " + ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        // Build the same response shape for every error so the API stays consistent.
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}

