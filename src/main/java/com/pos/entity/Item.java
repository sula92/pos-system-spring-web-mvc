package com.pos.entity;

import jakarta.persistence.*;

/**
 * Item Entity
 * Maps to 'items' table in PostgreSQL database.
 * This entity represents a product/item in the inventory.
 */
@Entity
@Table(name = "items")
@NamedQueries({
		// Search items by part of the description, ignoring letter case.
		@NamedQuery(
				name = "Item.findByDescriptionLike",
				query = "SELECT i FROM Item i WHERE LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))"
		)
})
@NamedNativeQueries({
		// Native SQL version that searches by code prefix.
		@NamedNativeQuery(
				name = "Item.findByCodePrefixNative",
				query = "SELECT * FROM items WHERE code LIKE CONCAT(:prefix, '%') ORDER BY code",
				resultClass = Item.class
		)
})
public class Item {

	@Id
	@Column(name = "code", length = 10)
	private String code;

	@Column(name = "description", length = 255, nullable = false)
	private String description;

	@Column(name = "unit_price", nullable = false)
	private double unitPrice;

	// One item has one inventory row. `mappedBy = "item"` means Inventory owns the relationship.
	// `cascade = ALL` keeps both sides in sync, and `orphanRemoval = true` deletes the inventory row
	// if the item is removed.
	@OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Inventory inventory;

	public Item() {}

	public Item(String code, String description, double unitPrice) {
		this.code = code;
		this.description = description;
		this.unitPrice = unitPrice;
	}

	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public double getUnitPrice() { return unitPrice; }
	public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

	public Inventory getInventory() { return inventory; }
	public void setInventory(Inventory inventory) { this.inventory = inventory; }
}


