package com.example.demo.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private boolean active = true;
    private Instant createdAt = Instant.now();

    // CONSTRUCTORS //
    public Cart() {}

    public Cart(Customer customer) {
        this.customer = customer;
    }

    // GETTERS //
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public boolean isActive() { return active; }
    public List<CartItem> getItems() { return items; }

    // SETTERS //
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setActive(boolean active) { this.active = active; }

    // CONVENIENCE METHODS
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
}
