package com.bookstore.pos.model;

import com.bookstore.demo.model.Customer;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    // CartItems relationship
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    // CONSTRUCTORS //
    public Cart() {}

    public Cart(Customer customer) {
        this.customer = customer;
    }

    // GETTERS //
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public List<CartItem> getItems() { return items; }

    // SETTERS //
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setItems(List<CartItem> items) { this.items = items; }

    // DB Management //
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
}
