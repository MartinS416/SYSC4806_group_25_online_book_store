package com.bookstore.pos.model;

/**
 * Payment processing state.
 */
public enum PaymentStatus {
    PENDING,
    AUTHORIZED,
    CAPTURED,
    FAILED,
    REFUNDED,
    CANCELED
}
