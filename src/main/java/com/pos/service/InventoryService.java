package com.pos.service;

import com.pos.dto.InventoryDTO;
import com.pos.dto.InventoryStockValueView;
import com.pos.entity.Inventory;
import com.pos.entity.Item;
import com.pos.exception.InvalidRequestException;
import com.pos.exception.ResourceNotFoundException;
import com.pos.projection.InventoryStockViewProjection;
import com.pos.repository.InventoryRepository;
import com.pos.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class InventoryService {

    private static final Logger logger = Logger.getLogger(InventoryService.class.getName());

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public InventoryDTO findInventory(String itemCode) {
        Inventory inventory = inventoryRepository.findById(itemCode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for item: " + itemCode));
        return toDTO(inventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryDTO> findAllInventories() {
        List<InventoryDTO> dtos = new ArrayList<>();
        for (Inventory inventory : inventoryRepository.findAllOrderByItemCode()) {
            dtos.add(toDTO(inventory));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<InventoryStockViewProjection> findInventoryStockViews() {
        return inventoryRepository.findInventoryStockViews();
    }

    @Transactional(readOnly = true)
    public List<InventoryStockValueView> findInventoryStockValueViews() {
        return inventoryRepository.findInventoryStockValueViews();
    }

    @Transactional(readOnly = true)
    public List<InventoryDTO> findLowStock(int maxQty) {
        List<InventoryDTO> dtos = new ArrayList<>();
        for (Inventory inventory : inventoryRepository.findByQtyLessThanEqualOrderByQtyAsc(maxQty)) {
            dtos.add(toDTO(inventory));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<InventoryDTO> findInStockAbove(int minQty) {
        List<InventoryDTO> dtos = new ArrayList<>();
        for (Inventory inventory : inventoryRepository.findByQtyGreaterThanOrderByQtyDesc(minQty)) {
            dtos.add(toDTO(inventory));
        }
        return dtos;
    }

    public InventoryDTO createInventory(InventoryDTO dto) {
        validate(dto);
        if (inventoryRepository.existsById(dto.getItemCode())) {
            throw new InvalidRequestException("Inventory already exists for item: " + dto.getItemCode());
        }

        Item item = itemRepository.findById(dto.getItemCode())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + dto.getItemCode()));

        Inventory saved = inventoryRepository.save(new Inventory(dto.getItemCode(), dto.getQty(), item));
        logger.info("Service: Inventory created for item: " + dto.getItemCode());
        return toDTO(saved);
    }

    public InventoryDTO updateInventory(InventoryDTO dto) {
        validate(dto);
        Inventory inventory = inventoryRepository.findById(dto.getItemCode())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for item: " + dto.getItemCode()));

        inventory.setQty(dto.getQty());
        Inventory updated = inventoryRepository.save(inventory);
        logger.info("Service: Inventory updated for item: " + dto.getItemCode());
        return toDTO(updated);
    }

    private void validate(InventoryDTO dto) {
        if (dto == null || dto.getItemCode() == null || dto.getItemCode().isEmpty()) {
            throw new InvalidRequestException("Item code is required for inventory operations");
        }
        if (dto.getQty() < 0) {
            throw new InvalidRequestException("Inventory quantity cannot be negative");
        }
    }

    private InventoryDTO toDTO(Inventory inventory) {
        return new InventoryDTO(inventory.getItemCode(), inventory.getQty());
    }
}

