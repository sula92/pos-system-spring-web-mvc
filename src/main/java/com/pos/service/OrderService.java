package com.pos.service;

import com.pos.dto.OrderDTO;
import com.pos.dto.OrderDetailDTO;
import com.pos.entity.OrderEntity;
import com.pos.entity.OrderDetailEntity;
import com.pos.entity.OrderDetailId;
import com.pos.entity.CustomerEntity;
import com.pos.entity.ItemEntity;
import com.pos.repository.OrderRepository;
import com.pos.repository.OrderDetailRepository;
import com.pos.repository.CustomerRepository;
import com.pos.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
            throw new IllegalArgumentException("Invalid order request");
        }

        // VALIDATION: Check if customer exists
        CustomerEntity customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + dto.getCustomerId()));
        logger.info("Service: Customer found: " + customer.getId());

        // VALIDATION: Pre-check all items are available and in stock
        Map<String, ItemEntity> itemCache = new HashMap<>();
        for (OrderDetailDTO detail : dto.getOrderDetails()) {
            if (!isValidOrderDetail(detail)) {
                throw new IllegalArgumentException("Invalid order detail for item: " + detail.getItemCode());
            }

            ItemEntity item = itemRepository.findById(detail.getItemCode())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + detail.getItemCode()));

            if (item.getQtyOnHAnd() < detail.getQty()) {
                throw new IllegalArgumentException("Insufficient stock for item: " + detail.getItemCode());
            }
            itemCache.put(detail.getItemCode(), item);
        }
        logger.info("Service: All items validated and in stock");

        // GENERATE: Create new order with existing date or today's date
        OrderEntity orderEntity = new OrderEntity(
                generateOrderId(),
                dto.getDate() != null ? dto.getDate() : LocalDate.now(),
                dto.getCustomerId()
        );
        logger.info("Service: Generated order ID: " + orderEntity.getOrderId());

        // SAVE: Persist the main order record
        OrderEntity savedOrder = orderRepository.save(orderEntity);
        logger.info("Service: Order record saved: " + savedOrder.getOrderId());

        // PROCESS: Save each order detail and update item stock
        for (OrderDetailDTO detail : dto.getOrderDetails()) {
            detail.setOrderId(savedOrder.getOrderId());

            // SAVE: Persist the order detail record
            OrderDetailId detailId = new OrderDetailId(savedOrder.getOrderId(), detail.getItemCode());
            OrderDetailEntity detailEntity = new OrderDetailEntity(
                    savedOrder.getOrderId(), detail.getItemCode(), detail.getQty(), detail.getUnitPrice());
            orderDetailRepository.save(detailEntity);
            logger.info("Service: Order detail saved: " + savedOrder.getOrderId() + " - " + detail.getItemCode());

            // UPDATE: Deduct stock from the Item table
            ItemEntity item = itemCache.get(detail.getItemCode());
            item.setQtyOnHAnd(item.getQtyOnHAnd() - detail.getQty());
            itemRepository.save(item);
            logger.info("Service: Item stock updated: " + detail.getItemCode());
        }

        logger.info("Service: Order transaction committed successfully for order: " + savedOrder.getOrderId());

        // BUILD: Return the complete order DTO with all details
        List<OrderDetailDTO> detailDTOs = new ArrayList<>();
        for (OrderDetailEntity d : orderDetailRepository.findByOrderId(savedOrder.getOrderId())) {
            detailDTOs.add(new OrderDetailDTO(d.getOrderId(), d.getItemCode(), d.getQty(), d.getUnitPrice()));
        }
        return new OrderDTO(savedOrder.getOrderId(), savedOrder.getDate(), savedOrder.getCustomerId(), detailDTOs);
    }

    /**
     * Find a specific order with all its details
     */
    @Transactional(readOnly = true)
    public OrderDTO findOrder(String id) {
        OrderEntity orderEntity = orderRepository.findById(id).orElse(null);
        if (orderEntity == null) return null;

        List<OrderDetailEntity> detailEntities = orderDetailRepository.findByOrderId(id);
        List<OrderDetailDTO> detailDTOs = new ArrayList<>();
        for (OrderDetailEntity d : detailEntities) {
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
        for (OrderEntity orderEntity : orderRepository.findAll()) {
            List<OrderDetailEntity> detailEntities = orderDetailRepository.findByOrderId(orderEntity.getOrderId());
            List<OrderDetailDTO> detailDTOs = new ArrayList<>();
            for (OrderDetailEntity d : detailEntities) {
                detailDTOs.add(new OrderDetailDTO(d.getOrderId(), d.getItemCode(), d.getQty(), d.getUnitPrice()));
            }
            dtos.add(new OrderDTO(orderEntity.getOrderId(), orderEntity.getDate(), orderEntity.getCustomerId(), detailDTOs));
        }
        return dtos;
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
        List<OrderEntity> allOrders = orderRepository.findAll();
        return "O" + (allOrders.size() + 1);
    }
}

