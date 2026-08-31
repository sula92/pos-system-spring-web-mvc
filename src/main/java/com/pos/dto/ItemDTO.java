package com.pos.dto;

public class ItemDTO {
    private String code;
    private String description;
    private double unitPrice;
    private int qtyOnHAnd;

    public ItemDTO() {}

    public ItemDTO(String code, String description, double unitPrice, int qtyOnHAnd) {
        this.code = code;
        this.description = description;
        this.unitPrice = unitPrice;
        this.qtyOnHAnd = qtyOnHAnd;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQtyOnHAnd() { return qtyOnHAnd; }
    public void setQtyOnHAnd(int qtyOnHAnd) { this.qtyOnHAnd = qtyOnHAnd; }
}

