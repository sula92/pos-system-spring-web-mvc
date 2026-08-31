package com.pos.entity;

import jakarta.persistence.*;

/**
 * Customer Entity
 * Maps to 'customers' table in PostgreSQL database.
 * This entity represents a customer in the POS system.
 */
@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "email", length = 150)
    private String email;

    public CustomerEntity() {}

    public CustomerEntity(String id, String name, String address, String email) {
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
