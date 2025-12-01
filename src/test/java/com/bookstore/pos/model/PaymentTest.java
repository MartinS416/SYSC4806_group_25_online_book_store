package com.bookstore.pos.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Payment} entity.
 */
class PaymentTest {

    /**
     * Verifies that basic fields such as amount and status can be set and retrieved.
     */
    @Test
    void canSetBasicFields() {
        Payment p = new Payment();
        p.setAmount(BigDecimal.TEN);
        p.setStatus(PaymentStatus.PENDING);

        assertEquals(BigDecimal.TEN, p.getAmount());
        assertEquals(PaymentStatus.PENDING, p.getStatus());
    }
}