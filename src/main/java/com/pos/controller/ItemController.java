package com.pos.controller;

import com.pos.service.ItemService;
import com.pos.dto.ItemDTO;
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
@RequestMapping("/item")
@CrossOrigin(origins = "http://localhost:5173")
public class ItemController {

    private static final Logger logger = Logger.getLogger(ItemController.class.getName());
    private static final Pattern CODE_PATTERN = Pattern.compile("^I\\d{3}$");

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<?> getItems(@RequestParam(name = "code", required = false) String code) {
        try {
            if (code != null) {
                if (!CODE_PATTERN.matcher(code).matches()) {
                    logger.warning("Invalid item code format: " + code);
                    return ResponseEntity.badRequest().body(errorResponse("Invalid item code format. Expected format: I followed by 3 digits (e.g. I001)"));
                }
                ItemDTO dto = itemService.findItem(code);
                if (dto == null) {
                    logger.info("Item not found: " + code);
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(dto);
            } else {
                logger.info("Fetching all items");
                List<ItemDTO> all = itemService.findAllItems();
                return ResponseEntity.ok(all);
            }
        } catch (Exception e) {
            logger.severe("Error fetching items: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Internal Server Error: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemDTO dto) {
        try {
            logger.info("Attempting to save new item");
            ItemDTO saved = itemService.saveItem(dto);
            logger.info("Item saved successfully with code: " + saved.getCode());
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("message", "Item saved successfully");
            resp.put("data", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            logger.severe("Error creating item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Internal Server Error: " + e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateItem(@RequestBody ItemDTO dto) {
        try {
            String code = dto.getCode();
            logger.info("Attempting to update item: " + code);
            if (code == null || !CODE_PATTERN.matcher(code).matches()) {
                logger.warning("Invalid or missing item code for update: " + code);
                return ResponseEntity.badRequest().body(errorResponse("Invalid or missing item code. Expected format: I followed by 3 digits (e.g. I001)"));
            }
            ItemDTO updated = itemService.updateItem(dto);
            logger.info("Item updated successfully: " + code);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.severe("Error updating item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Internal Server Error: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteItem(@RequestParam("code") String code) {
        try {
            logger.info("Attempting to delete item: " + code);
            if (code == null || !CODE_PATTERN.matcher(code).matches()) {
                logger.warning("Invalid item code format for delete: " + code);
                return ResponseEntity.badRequest().body(errorResponse("Invalid item code format. Expected format: I followed by 3 digits (e.g. I001)"));
            }
            itemService.deleteItem(code);
            logger.info("Item deleted successfully: " + code);
            return ResponseEntity.ok(successResponse("Item deleted successfully"));
        } catch (Exception e) {
            logger.severe("Error deleting item: " + e.getMessage());
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
