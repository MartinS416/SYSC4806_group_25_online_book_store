package com.bookstore.pos.model;

/**
 * Order lifecycle states.
 */
public enum OrderStatus {
    NEW,
    PROCESSING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELED
}
