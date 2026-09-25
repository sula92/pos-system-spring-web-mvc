package com.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity
 * Maps to 'orders' table in PostgreSQL database.
 * This entity represents a sales order in the POS system.
 */
@Entity
@Table(name = "orders")
@NamedQueries({
		@NamedQuery(
				name = "Order.findByCustomerId",
				query = "SELECT o FROM Order o WHERE o.customerId = :customerId ORDER BY o.date DESC"
		),
		@NamedQuery(
				name = "Order.findRecent",
				query = "SELECT o FROM Order o WHERE o.date >= :fromDate ORDER BY o.date DESC"
		)
})
@NamedNativeQueries({
		@NamedNativeQuery(
				name = "Order.findByCustomerIdNative",
				query = "SELECT * FROM orders WHERE customer_id = :customerId ORDER BY date DESC",
				resultClass = Order.class
		),
		@NamedNativeQuery(
				name = "Order.findByDateRangeNative",
				query = "SELECT * FROM orders WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC",
				resultClass = Order.class
		)
})
@NamedEntityGraphs({
		@NamedEntityGraph(
				name = "Order.withDetails",
				attributeNodes = @NamedAttributeNode("orderDetails")
		)
})
public class Order {

	@Id
	@Column(name = "order_id", length = 10)
	private String orderId;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "customer_id", length = 10, nullable = false)
	private String customerId;

	// Relationship to Order Details
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<OrderDetail> orderDetails = new ArrayList<>();

	public Order() {}

	public Order(String orderId, LocalDate date, String customerId) {
		this.orderId = orderId;
		this.date = date;
		this.customerId = customerId;
	}

	public String getOrderId() { return orderId; }
	public void setOrderId(String orderId) { this.orderId = orderId; }

	public LocalDate getDate() { return date; }
	public void setDate(LocalDate date) { this.date = date; }

	public String getCustomerId() { return customerId; }
	public void setCustomerId(String customerId) { this.customerId = customerId; }

	public List<OrderDetail> getOrderDetails() { return orderDetails; }
	public void setOrderDetails(List<OrderDetail> orderDetails) { this.orderDetails = orderDetails; }
}


