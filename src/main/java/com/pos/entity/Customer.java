package com.pos.entity;

import jakarta.persistence.*;

/**
 * Customer Entity
 * Maps to 'customers' table in PostgreSQL database.
 * This entity represents a customer in the POS system.
 */
@Entity
@Table(name = "customers")
@NamedQueries({
        // JPQL query: find one customer by email.
        @NamedQuery(
                name = "Customer.findByEmail",
                query = "SELECT c FROM Customer c WHERE c.email = :email"
        ),
        // JPQL query: return all customers ordered by name.
        @NamedQuery(
                name = "Customer.findAllOrderByName",
                query = "SELECT c FROM Customer c ORDER BY c.name"
        )
})
@NamedNativeQueries({
        // Native SQL version of the email lookup.
        @NamedNativeQuery(
                name = "Customer.findByEmailNative",
                query = "SELECT * FROM customers WHERE email = :email",
                resultClass = Customer.class
        ),
        // Native SQL version of the alphabetical list.
        @NamedNativeQuery(
                name = "Customer.findAllNativeOrderByName",
                query = "SELECT * FROM customers ORDER BY name",
                resultClass = Customer.class
        )
})
public class Customer {

    // Primary key for the customer table.
    @Id
    @Column(name = "id", length = 10)
    private String id;

    // Customer name is required, so nullable=false protects the database column.
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    // Optional address line.
    @Column(name = "address", length = 255)
    private String address;

    // Optional email used by the lookup query above.
    @Column(name = "email", length = 150)
    private String email;

    public Customer() {}

    public Customer(String id, String name, String address, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

