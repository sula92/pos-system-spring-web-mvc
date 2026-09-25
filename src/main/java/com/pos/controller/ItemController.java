package com.pos.controller;

import com.pos.service.ItemService;
import com.pos.dto.ItemDTO;
import com.pos.exception.InvalidRequestException;
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
        if (code != null) {
            if (!CODE_PATTERN.matcher(code).matches()) {
                logger.warning("Invalid item code format: " + code);
                throw new InvalidRequestException("Invalid item code format. Expected format: I followed by 3 digits (e.g. I001)");
            }
            return ResponseEntity.ok(itemService.findItem(code));
        }

        logger.info("Fetching all items");
        List<ItemDTO> all = itemService.findAllItems();
        return ResponseEntity.ok(all);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemDTO dto) {
        logger.info("Attempting to save new item");
        ItemDTO saved = itemService.saveItem(dto);
        logger.info("Item saved successfully with code: " + saved.getCode());
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("message", "Item saved successfully");
        resp.put("data", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping
    public ResponseEntity<?> updateItem(@RequestBody ItemDTO dto) {
        String code = dto.getCode();
        logger.info("Attempting to update item: " + code);
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            logger.warning("Invalid or missing item code for update: " + code);
            throw new InvalidRequestException("Invalid or missing item code. Expected format: I followed by 3 digits (e.g. I001)");
        }
        ItemDTO updated = itemService.updateItem(dto);
        logger.info("Item updated successfully: " + code);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteItem(@RequestParam("code") String code) {
        logger.info("Attempting to delete item: " + code);
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            logger.warning("Invalid item code format for delete: " + code);
            throw new InvalidRequestException("Invalid item code format. Expected format: I followed by 3 digits (e.g. I001)");
        }
        itemService.deleteItem(code);
        logger.info("Item deleted successfully: " + code);
        return ResponseEntity.ok(successResponse("Item deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItems(@RequestParam("keyword") String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new InvalidRequestException("Keyword is required for item search");
        }
        return ResponseEntity.ok(itemService.searchItemsByDescription(keyword));
    }

    @GetMapping("/by-min-price")
    public ResponseEntity<List<ItemDTO>> getItemsByMinPrice(@RequestParam("minPrice") double minPrice) {
        if (minPrice < 0) {
            throw new InvalidRequestException("minPrice cannot be negative");
        }
        return ResponseEntity.ok(itemService.findItemsByMinPrice(minPrice));
    }

    private Map<String, Object> successResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        return response;
    }
}
