package com.pos.controller;

import com.pos.service.CustomerService;
import com.pos.dto.CustomerPurchaseStatsDTO;
import com.pos.dto.CustomerDTO;
import com.pos.exception.InvalidRequestException;
import com.pos.projection.CustomerPurchaseStatsProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.logging.Logger;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    private static final Logger logger = Logger.getLogger(CustomerController.class.getName());
    private static final Pattern ID_PATTERN = Pattern.compile("^C\\d{3}$");

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<?> getCustomers(@RequestParam(name = "id", required = false) String id) {
        if (id != null) {
            if (!ID_PATTERN.matcher(id).matches()) {
                logger.warning("Invalid customer ID format: " + id);
                throw new InvalidRequestException("Invalid customer ID format. Expected format: C followed by 3 digits (e.g. C001)");
            }
            return ResponseEntity.ok(customerService.findCustomer(id));
        }

        logger.info("Fetching all customers");
        List<CustomerDTO> all = customerService.findAllCustomers();
        return ResponseEntity.ok(all);
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDTO dto) {
        logger.info("Attempting to save new customer");
        CustomerDTO saved = customerService.saveCustomer(dto);
        logger.info("Customer saved successfully with ID: " + saved.getId());
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("message", "Customer saved successfully");
        resp.put("data", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerDTO dto) {
        String id = dto.getId();
        logger.info("Attempting to update customer: " + id);
        if (id == null || !ID_PATTERN.matcher(id).matches()) {
            logger.warning("Invalid or missing customer ID for update: " + id);
            throw new InvalidRequestException("Invalid or missing customer ID. Expected format: C followed by 3 digits (e.g. C001)");
        }
        CustomerDTO updated = customerService.updateCustomer(dto);
        logger.info("Customer updated successfully: " + id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCustomer(@RequestParam("id") String id) {
        logger.info("Attempting to delete customer: " + id);
        if (id == null || !ID_PATTERN.matcher(id).matches()) {
            logger.warning("Invalid customer ID format for delete: " + id);
            throw new InvalidRequestException("Invalid customer ID format. Expected format: C followed by 3 digits (e.g. C001)");
        }
        customerService.deleteCustomer(id);
        logger.info("Customer deleted successfully: " + id);
        return ResponseEntity.ok(successResponse("Customer deleted successfully"));
    }

    @GetMapping("/purchase-stats")
    public ResponseEntity<List<CustomerPurchaseStatsProjection>> getCustomerPurchaseStats() {
        return ResponseEntity.ok(customerService.findCustomerPurchaseStats());
    }

    @GetMapping("/purchase-stats-dto")
    public ResponseEntity<List<CustomerPurchaseStatsDTO>> getCustomerPurchaseStatsDto() {
        return ResponseEntity.ok(customerService.findCustomerPurchaseStatsDto());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRequestException("Name is required for customer search");
        }
        return ResponseEntity.ok(customerService.searchCustomersByName(name));
    }

    @GetMapping("/by-email")
    public ResponseEntity<CustomerDTO> getCustomerByEmail(@RequestParam("email") String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidRequestException("Email is required");
        }
        return ResponseEntity.ok(customerService.findCustomerByEmail(email));
    }

    private Map<String, Object> successResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        return response;
    }
}
