package com.pos.controller;

import com.pos.service.CustomerService;
import com.pos.dto.CustomerDTO;
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
        try {
            if (id != null) {
                if (!ID_PATTERN.matcher(id).matches()) {
                    logger.warning("Invalid customer ID format: " + id);
                    return ResponseEntity.badRequest().body(errorResponse("Invalid customer ID format. Expected format: C followed by 3 digits (e.g. C001)"));
                }
                CustomerDTO dto = customerService.findCustomer(id);
                if (dto == null) {
                    logger.info("Customer not found: " + id);
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(dto);
            } else {
                logger.info("Fetching all customers");
                List<CustomerDTO> all = customerService.findAllCustomers();
                return ResponseEntity.ok(all);
            }
        } catch (Exception e) {
            logger.severe("Error fetching customers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Internal Server Error: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDTO dto) {
        try {
            logger.info("Attempting to save new customer");
            CustomerDTO saved = customerService.saveCustomer(dto);
            logger.info("Customer saved successfully with ID: " + saved.getId());
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("message", "Customer saved successfully");
            resp.put("data", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            logger.severe("Error creating customer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Internal Server Error: " + e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerDTO dto) {
        try {
            String id = dto.getId();
            logger.info("Attempting to update customer: " + id);
            if (id == null || !ID_PATTERN.matcher(id).matches()) {
                logger.warning("Invalid or missing customer ID for update: " + id);
                return ResponseEntity.badRequest().body(errorResponse("Invalid or missing customer ID. Expected format: C followed by 3 digits (e.g. C001)"));
            }
            CustomerDTO updated = customerService.updateCustomer(dto);
            logger.info("Customer updated successfully: " + id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.severe("Error updating customer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Internal Server Error: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCustomer(@RequestParam("id") String id) {
        try {
            logger.info("Attempting to delete customer: " + id);
            if (id == null || !ID_PATTERN.matcher(id).matches()) {
                logger.warning("Invalid customer ID format for delete: " + id);
                return ResponseEntity.badRequest().body(errorResponse("Invalid customer ID format. Expected format: C followed by 3 digits (e.g. C001)"));
            }
            customerService.deleteCustomer(id);
            logger.info("Customer deleted successfully: " + id);
            return ResponseEntity.ok(successResponse("Customer deleted successfully"));
        } catch (Exception e) {
            logger.severe("Error deleting customer: " + e.getMessage());
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
