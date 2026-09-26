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
        // JPQL query: load all line items for one order in item-code order.
		@NamedQuery(
				name = "OrderDetail.findByOrderId",
				query = "SELECT od FROM OrderDetail od WHERE od.id.orderId = :orderId ORDER BY od.id.itemCode"
		),
        // Aggregates total quantity sold for one item code.
		@NamedQuery(
				name = "OrderDetail.totalQtyByItemCode",
				query = "SELECT COALESCE(SUM(od.qty), 0) FROM OrderDetail od WHERE od.id.itemCode = :itemCode"
		)
})
@NamedNativeQueries({
        // Native SQL version of the same lookup, useful when the exact table columns matter.
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

	// Embedded composite ID made of orderId + itemCode.
	@EmbeddedId
	private OrderDetailId id;

	// Quantity of this item in the order line.
	@Column(name = "qty", nullable = false)
	private int qty;

	// Price of the item at the time the order was placed.
	@Column(name = "unit_price", nullable = false)
	private double unitPrice;

	// `insertable=false` and `updatable=false` are needed because order_id is already stored in the embedded ID.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", insertable = false, updatable = false)
	private Order order;

	public OrderDetail() {
		// Create the embedded ID early so the getter/setter methods can safely delegate to it.
		this.id = new OrderDetailId();
	}

	public OrderDetail(String orderId, String itemCode, int qty, double unitPrice) {
		// Build the composite key from the two values that uniquely identify this row.
		this.id = new OrderDetailId(orderId, itemCode);
		this.qty = qty;
		this.unitPrice = unitPrice;
	}

	public OrderDetailId getId() { return id; }
	public void setId(OrderDetailId id) { this.id = id; }

	// Delegate these accessors to the embedded ID so callers do not need to touch OrderDetailId directly.
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


