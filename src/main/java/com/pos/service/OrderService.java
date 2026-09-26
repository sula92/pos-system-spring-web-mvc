package com.pos.service;

import com.pos.dto.OrderDTO;
import com.pos.dto.OrderDetailDTO;
import com.pos.dto.OrderSummaryDTO;
import com.pos.entity.Inventory;
import com.pos.entity.Order;
import com.pos.entity.OrderDetail;
import com.pos.entity.Customer;
import com.pos.exception.InsufficientStockException;
import com.pos.exception.InvalidRequestException;
import com.pos.exception.ResourceNotFoundException;
import com.pos.projection.OrderSummaryProjection;
import com.pos.repository.OrderRepository;
import com.pos.repository.OrderDetailRepository;
import com.pos.repository.CustomerRepository;
import com.pos.repository.InventoryRepository;
import com.pos.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * OrderService - Business Logic Layer
 * Handles all order-related operations with ACID transaction support.
 * Spring's @Transactional annotation ensures atomicity: all operations succeed or all rollback.
 * Hibernate manages the entire transaction lifecycle.
 */
@Service
@Transactional
public class OrderService {

    private static final Logger logger = Logger.getLogger(OrderService.class.getName());

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Place an order with full ACID transaction support.
     * This method ensures:
     * 1. Customer exists
     * 2. All items are available and in stock
     * 3. Order is saved
     * 4. Order details are saved
     * 5. Item stock is decremented
     *
     * If ANY step fails, the entire transaction is rolled back automatically.
     * @param dto Order DTO containing order details
     * @return OrderDTO with generated order ID
     */
    public OrderDTO placeOrder(OrderDTO dto) {
        logger.info("Service: Attempting to place order for customer: " + dto.getCustomerId());

        // VALIDATION: Check if order request is valid
        if (!isValidOrderRequest(dto)) {
            logger.warning("Service: Invalid order request");
            throw new InvalidRequestException("Invalid order request");
        }

        // VALIDATION: Check if customer exists
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + dto.getCustomerId()));
        logger.info("Service: Customer found: " + customer.getId());

        // If the same item appears more than once, add its quantities together first.
        // That way we check stock against the real total before saving anything.
        Map<String, Integer> requestedQtyByItem = new HashMap<>();
        for (OrderDetailDTO detail : dto.getOrderDetails()) {
            if (!isValidOrderDetail(detail)) {
                throw new InvalidRequestException("Invalid order detail for item: " + detail.getItemCode());
            }

            itemRepository.findById(detail.getItemCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + detail.getItemCode()));

            requestedQtyByItem.merge(detail.getItemCode(), detail.getQty(), Integer::sum);
        }

        // VALIDATION: Pre-check all items are available and in stock
        Map<String, Inventory> inventoryCache = new HashMap<>();
        for (Map.Entry<String, Integer> requestedQty : requestedQtyByItem.entrySet()) {
            String itemCode = requestedQty.getKey();
            int totalRequestedQty = requestedQty.getValue();

            // Lock the inventory row while we check it so two orders cannot reduce the same stock at once.
            Inventory inventory = inventoryRepository.findByItemCodeForUpdate(itemCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for item: " + itemCode));

            if (inventory.getQty() < totalRequestedQty) {
                throw new InsufficientStockException("Insufficient stock for item: " + itemCode);
            }
            inventoryCache.put(itemCode, inventory);
        }
        logger.info("Service: All items validated and in stock");

        // GENERATE: Create new order with existing date or today's date
        Order orderEntity = new Order(
                generateOrderId(),
                dto.getDate() != null ? dto.getDate() : LocalDate.now(),
                dto.getCustomerId()
        );
        logger.info("Service: Generated order ID: " + orderEntity.getOrderId());

        // SAVE: Persist the main order record
        Order savedOrder = orderRepository.save(orderEntity);
        logger.info("Service: Order record saved: " + savedOrder.getOrderId());

        // Save each order detail and reduce stock in the same transaction.
        // If any save fails, Spring rolls back the whole order.
        for (OrderDetailDTO detail : dto.getOrderDetails()) {
            detail.setOrderId(savedOrder.getOrderId());

            // SAVE: Persist the order detail record
            OrderDetail detailEntity = new OrderDetail(
                    savedOrder.getOrderId(), detail.getItemCode(), detail.getQty(), detail.getUnitPrice());
            orderDetailRepository.save(detailEntity);
            logger.info("Service: Order detail saved: " + savedOrder.getOrderId() + " - " + detail.getItemCode());

            // Reduce the in-memory inventory value, then persist the change.
            Inventory inventory = inventoryCache.get(detail.getItemCode());
            inventory.setQty(inventory.getQty() - detail.getQty());
            inventoryRepository.save(inventory);
            logger.info("Service: Item stock updated: " + detail.getItemCode());
        }

        logger.info("Service: Order transaction committed successfully for order: " + savedOrder.getOrderId());

        // BUILD: Return the complete order DTO with all details
        List<OrderDetailDTO> detailDTOs = new ArrayList<>();
        for (OrderDetail d : orderDetailRepository.findByOrderId(savedOrder.getOrderId())) {
            detailDTOs.add(new OrderDetailDTO(d.getOrderId(), d.getItemCode(), d.getQty(), d.getUnitPrice()));
        }
        return new OrderDTO(savedOrder.getOrderId(), savedOrder.getDate(), savedOrder.getCustomerId(), detailDTOs);
    }

    /**
     * Find a specific order with all its details
     */
    @Transactional(readOnly = true)
    public OrderDTO findOrder(String id) {
        // JOIN FETCH loads the order and its details together instead of making extra queries later.
        Order orderEntity = orderRepository.findByIdJoinFetch(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        List<OrderDetailDTO> detailDTOs = new ArrayList<>();
        for (OrderDetail d : orderEntity.getOrderDetails()) {
            detailDTOs.add(new OrderDetailDTO(d.getOrderId(), d.getItemCode(), d.getQty(), d.getUnitPrice()));
        }
        return new OrderDTO(orderEntity.getOrderId(), orderEntity.getDate(), orderEntity.getCustomerId(), detailDTOs);
    }

    /**
     * Find all orders with their details
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> findAllOrders() {
        List<OrderDTO> dtos = new ArrayList<>();
        // This avoids the N+1 query problem when converting many orders to DTOs.
        for (Order orderEntity : orderRepository.findAllJoinFetch()) {
            List<OrderDetailDTO> detailDTOs = new ArrayList<>();
            for (OrderDetail d : orderEntity.getOrderDetails()) {
                detailDTOs.add(new OrderDetailDTO(d.getOrderId(), d.getItemCode(), d.getQty(), d.getUnitPrice()));
            }
            dtos.add(new OrderDTO(orderEntity.getOrderId(), orderEntity.getDate(), orderEntity.getCustomerId(), detailDTOs));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryProjection> findOrderSummaries() {
        return orderRepository.findOrderSummaries();
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryDTO> findOrderSummariesDto() {
        return orderRepository.findOrderSummariesDto();
    }

    /**
     * Validate the order request DTO
     */
    private boolean isValidOrderRequest(OrderDTO dto) {
        return dto != null
                && dto.getCustomerId() != null
                && !dto.getCustomerId().isEmpty()
                && dto.getOrderDetails() != null
                && !dto.getOrderDetails().isEmpty();
    }

    /**
     * Validate individual order detail
     */
    private boolean isValidOrderDetail(OrderDetailDTO detail) {
        return detail != null
                && detail.getItemCode() != null
                && !detail.getItemCode().isEmpty()
                && detail.getQty() > 0
                && detail.getUnitPrice() >= 0;
    }

    /**
     * Generate a new order ID (simple sequential format: O1, O2, O3, etc.)
     * In a real system, this would use database sequences or UUID
     */
    private String generateOrderId() {
        List<Order> allOrders = orderRepository.findAll();
        return "O" + (allOrders.size() + 1);
    }
}

