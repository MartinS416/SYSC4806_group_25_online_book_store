package com.example.demo.model;

import jakarta.persistence.*;

/**
 * Feature --> Extend architecture to accommodate multiple merchants/ stores.
 */
@Entity
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // CONSTRUCTORS //
    public Merchant() {}

    public Merchant(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // GETTERS //
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // SETTERS //
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
