package com.pos.controller;

import com.pos.service.OrderService;
import com.pos.dto.OrderDTO;
import com.pos.dto.OrderSummaryDTO;
import com.pos.exception.InvalidRequestException;
import com.pos.projection.OrderSummaryProjection;
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
    // Order IDs are expected to look like O001, O002, O123, etc.
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("^O\\d{3}$");

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam(name = "id", required = false) String id) {
        if (id != null) {
            // If the client asks for one order, make sure the ID format is valid first.
            if (!ORDER_ID_PATTERN.matcher(id).matches()) {
                logger.warning("Invalid order ID format: " + id);
                throw new InvalidRequestException("Invalid order ID format. Expected format: O followed by 3 digits (e.g. O001)");
            }
            return ResponseEntity.ok(orderService.findOrder(id));
        }

        logger.info("Fetching all orders");
        List<OrderDTO> all = orderService.findAllOrders();
        return ResponseEntity.ok(all);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO dto) {
        logger.info("Attempting to place new order");
        // If the client does not send a date, use today so the order still has a valid date.
        if (dto.getDate() == null) {
            dto.setDate(LocalDate.now());
        }
        OrderDTO placed = orderService.placeOrder(dto);
        logger.info("Order placed successfully with ID: " + placed.getOrderId());
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("message", "Order placed successfully");
        resp.put("data", placed);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<OrderSummaryProjection>> getOrderSummaries() {
        // Projection endpoint: returns a lightweight report view instead of full order entities.
        return ResponseEntity.ok(orderService.findOrderSummaries());
    }

    @GetMapping("/summary-dto")
    public ResponseEntity<List<OrderSummaryDTO>> getOrderSummariesDto() {
        // Same report data, but mapped into a DTO class.
        return ResponseEntity.ok(orderService.findOrderSummariesDto());
    }

}
