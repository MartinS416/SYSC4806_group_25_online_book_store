package com.example.demo.model;

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
