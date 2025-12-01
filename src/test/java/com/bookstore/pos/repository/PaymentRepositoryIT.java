package com.bookstore.pos.repository;

import com.bookstore.pos.model.Payment;
import com.bookstore.pos.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link PaymentRepository}.
 * <p>
 * Level: integration.
 * Verifies basic persistence of payment entities used by POS checkout flows.
 */
@DataJpaTest
class PaymentRepositoryIT {

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Verifies that a payment can be persisted and retrieved by id.
     */
    @Test
    void saveAndFindById_persistsPayment() {
        Payment p = new Payment();
        p.setAmount(BigDecimal.TEN);
        p.setStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(p);

        Optional<Payment> found = paymentRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(BigDecimal.TEN, found.get().getAmount());
        assertEquals(PaymentStatus.PENDING, found.get().getStatus());
    }
}