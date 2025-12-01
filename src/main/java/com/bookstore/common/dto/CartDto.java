package com.bookstore.common.dto;

import java.util.List;

public class CartDto {
    private Long id;
    private Long customerId;
    private boolean active;
    private List<CartItemDto> items;

    public CartDto() {}
    public CartDto(Long id, Long customerId, boolean active, List<CartItemDto> items) {
        this.id = id;
        this.customerId = customerId;
        this.active = active;
        this.items = items;
    }

    // GETTERS //
    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public boolean isActive() { return active; }
    public List<CartItemDto> getItems() { return items; }

    // SETTERS //
    public void setId(Long id) { this.id = id; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setActive(boolean active) { this.active = active; }
    public void setItems(List<CartItemDto> items) { this.items = items; }
}