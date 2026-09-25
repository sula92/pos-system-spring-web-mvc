package com.pos.entity;

import jakarta.persistence.*;

/**
 * Order Detail Entity
 * Maps to 'order_details' table in PostgreSQL database.
 * This entity represents individual line items in an order.
 * Uses a composite primary key (order_id, item_code).
 */
@Entity
@Table(name = "order_details")
@NamedQueries({
		@NamedQuery(
				name = "OrderDetail.findByOrderId",
				query = "SELECT od FROM OrderDetail od WHERE od.id.orderId = :orderId ORDER BY od.id.itemCode"
		),
		@NamedQuery(
				name = "OrderDetail.totalQtyByItemCode",
				query = "SELECT COALESCE(SUM(od.qty), 0) FROM OrderDetail od WHERE od.id.itemCode = :itemCode"
		)
})
@NamedNativeQueries({
		@NamedNativeQuery(
				name = "OrderDetail.findByOrderIdNative",
				query = "SELECT * FROM order_details WHERE order_id = :orderId ORDER BY item_code",
				resultClass = OrderDetail.class
		),
		@NamedNativeQuery(
				name = "OrderDetail.findHighQtyNative",
				query = "SELECT * FROM order_details WHERE qty >= :minQty ORDER BY qty DESC",
				resultClass = OrderDetail.class
		)
})
public class OrderDetail {

	@EmbeddedId
	private OrderDetailId id;

	@Column(name = "qty", nullable = false)
	private int qty;

	@Column(name = "unit_price", nullable = false)
	private double unitPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", insertable = false, updatable = false)
	private Order order;

	public OrderDetail() {
		this.id = new OrderDetailId();
	}

	public OrderDetail(String orderId, String itemCode, int qty, double unitPrice) {
		this.id = new OrderDetailId(orderId, itemCode);
		this.qty = qty;
		this.unitPrice = unitPrice;
	}

	public OrderDetailId getId() { return id; }
	public void setId(OrderDetailId id) { this.id = id; }

	public String getOrderId() { return id.getOrderId(); }
	public void setOrderId(String orderId) { this.id.setOrderId(orderId); }

	public String getItemCode() { return id.getItemCode(); }
	public void setItemCode(String itemCode) { this.id.setItemCode(itemCode); }

	public int getQty() { return qty; }
	public void setQty(int qty) { this.qty = qty; }

	public double getUnitPrice() { return unitPrice; }
	public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

	public Order getOrder() { return order; }
	public void setOrder(Order order) { this.order = order; }
}


