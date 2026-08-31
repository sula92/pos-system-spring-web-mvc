package com.pos.controller;

import com.pos.service.OrderService;
import com.pos.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.logging.Logger;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private static final Logger logger = Logger.getLogger(OrderController.class.getName());
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("^O\\d{3}$");

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam(name = "id", required = false) String id) {
        try {
            if (id != null) {
                if (!ORDER_ID_PATTERN.matcher(id).matches()) {
                    logger.warning("Invalid order ID format: " + id);
                    return ResponseEntity.badRequest().body(errorResponse("Invalid order ID format. Expected format: O followed by 3 digits (e.g. O001)"));
                }
                OrderDTO dto = orderService.findOrder(id);
                if (dto == null) {
                    logger.info("Order not found: " + id);
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(dto);
            } else {
                logger.info("Fetching all orders");
                List<OrderDTO> all = orderService.findAllOrders();
                return ResponseEntity.ok(all);
            }
        } catch (Exception e) {
            logger.severe("Error fetching orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Internal Server Error: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO dto) {
        try {
            logger.info("Attempting to place new order");
            // Set default date if not provided
            if (dto.getDate() == null) {
                dto.setDate(LocalDate.now());
            }
            OrderDTO placed = orderService.placeOrder(dto);
            logger.info("Order placed successfully with ID: " + placed.getOrderId());
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("message", "Order placed successfully");
            resp.put("data", placed);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            logger.severe("Error creating order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Internal Server Error: " + e.getMessage()));
        }
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", message);
        return response;
    }

    private Map<String, Object> successResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        return response;
    }
}
