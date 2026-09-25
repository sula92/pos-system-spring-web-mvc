package com.pos.controller;

import com.pos.dto.InventoryDTO;
import com.pos.dto.InventoryStockValueView;
import com.pos.exception.InvalidRequestException;
import com.pos.projection.InventoryStockViewProjection;
import com.pos.service.InventoryService;
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
@RequestMapping("/inventory")
@CrossOrigin(origins = "http://localhost:5173")
public class InventoryController {

    private static final Logger logger = Logger.getLogger(InventoryController.class.getName());
    private static final Pattern CODE_PATTERN = Pattern.compile("^I\\d{3}$");

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<?> getInventory(@RequestParam(name = "code", required = false) String code) {
        if (code != null) {
            if (!CODE_PATTERN.matcher(code).matches()) {
                throw new InvalidRequestException("Invalid item code format. Expected format: I followed by 3 digits (e.g. I001)");
            }
            return ResponseEntity.ok(inventoryService.findInventory(code));
        }

        List<InventoryDTO> all = inventoryService.findAllInventories();
        return ResponseEntity.ok(all);
    }

    @PostMapping
    public ResponseEntity<?> createInventory(@RequestBody InventoryDTO dto) {
        validateCode(dto.getItemCode());
        InventoryDTO saved = inventoryService.createInventory(dto);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Inventory created successfully");
        response.put("data", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<?> updateInventory(@RequestBody InventoryDTO dto) {
        validateCode(dto.getItemCode());
        InventoryDTO updated = inventoryService.updateInventory(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/stock-view")
    public ResponseEntity<List<InventoryStockViewProjection>> getInventoryStockView() {
        return ResponseEntity.ok(inventoryService.findInventoryStockViews());
    }

    @GetMapping("/stock-value-view")
    public ResponseEntity<List<InventoryStockValueView>> getInventoryStockValueView() {
        return ResponseEntity.ok(inventoryService.findInventoryStockValueViews());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStock(@RequestParam("maxQty") int maxQty) {
        if (maxQty < 0) {
            throw new InvalidRequestException("maxQty cannot be negative");
        }
        return ResponseEntity.ok(inventoryService.findLowStock(maxQty));
    }

    @GetMapping("/in-stock")
    public ResponseEntity<List<InventoryDTO>> getInStockAbove(@RequestParam("minQty") int minQty) {
        if (minQty < 0) {
            throw new InvalidRequestException("minQty cannot be negative");
        }
        return ResponseEntity.ok(inventoryService.findInStockAbove(minQty));
    }

    private void validateCode(String code) {
        logger.info("Validating inventory code: " + code);
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            throw new InvalidRequestException("Invalid or missing item code. Expected format: I followed by 3 digits (e.g. I001)");
        }
    }
}

