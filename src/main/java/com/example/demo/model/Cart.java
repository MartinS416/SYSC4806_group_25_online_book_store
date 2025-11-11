package com.example.demo.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // owning side of the relationship to Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // active flag (true = current cart), totals, timestamps, etc.
    private boolean active = true;
    private Instant createdAt = Instant.now();

    // CONSTRUCTORS //
    public Cart() {}

    public Cart(Customer customer) { this.customer = customer; }

    // GETTERS //
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }

    // SETTERS //
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
