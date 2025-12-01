package com.bookstore.pos.service;

import com.bookstore.pos.model.Payment;
import com.bookstore.pos.model.PaymentStatus;
import com.bookstore.pos.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PaymentService}.
 * <p>
 * These tests exercise payment creation, retrieval, status changes,
 * deletion, and simple card data validation logic. All persistence
 * interactions are mocked via {@link PaymentRepository}.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    /**
     * Verifies that {@link PaymentService#create(Payment)} sets the payment
     * status to {@link PaymentStatus#PENDING}, initializes {@code createdAt},
     * and persists the entity.
     */
    @Test
    void create_setsPendingStatusAndCreatedAt() {
        Payment payment = new Payment();
        payment.setAmount(BigDecimal.TEN);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment saved = paymentService.create(payment);

        assertEquals(PaymentStatus.PENDING, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
        verify(paymentRepository).save(saved);
    }

    /**
     * Verifies that {@link PaymentService#create(Payment)} rejects {@code null}
     * input by throwing an {@link IllegalArgumentException}.
     */
    @Test
    void create_nullPayment_throws() {
        assertThrows(IllegalArgumentException.class, () -> paymentService.create(null));
        verifyNoInteractions(paymentRepository);
    }

    /**
     * Verifies that {@link PaymentService#findById(Long)} returns the payment
     * when the repository finds a matching entity.
     */
    @Test
    void findById_existingPayment_returnsPayment() {
        Payment p = new Payment();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));

        Payment found = paymentService.findById(1L);

        assertSame(p, found);
    }

    /**
     * Verifies that {@link PaymentService#findById(Long)} throws an
     * {@link IllegalArgumentException} when no payment is found.
     */
    @Test
    void findById_missingPayment_throws() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> paymentService.findById(99L));
    }

    /**
     * Verifies that {@link PaymentService#markFailed(Long, String)} updates
     * the payment status to {@link PaymentStatus#FAILED} and persists it.
     */
    @Test
    void markFailed_updatesStatusToFailed() {
        Payment existing = new Payment();
        existing.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(paymentRepository.save(existing)).thenReturn(existing);

        Payment updated = paymentService.markFailed(1L, "declined");

        assertEquals(PaymentStatus.FAILED, updated.getStatus());
        verify(paymentRepository).save(existing);
    }

    /**
     * Verifies that {@link PaymentService#delete(Long)} deletes an existing
     * payment and does not throw an exception.
     */
    @Test
    void delete_existingPayment_deletes() {
        when(paymentRepository.existsById(5L)).thenReturn(true);

        paymentService.delete(5L);

        verify(paymentRepository).deleteById(5L);
    }

    /**
     * Verifies that {@link PaymentService#delete(Long)} throws an
     * {@link IllegalArgumentException} when attempting to delete a
     * non-existing payment.
     */
    @Test
    void delete_missingPayment_throws() {
        when(paymentRepository.existsById(5L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> paymentService.delete(5L));
        verify(paymentRepository, never()).deleteById(anyLong());
    }

    /**
     * Verifies that {@link PaymentService#exists(Long)} delegates to
     * {@link PaymentRepository#existsById(Object)}.
     */
    @Test
    void exists_delegatesToRepository() {
        when(paymentRepository.existsById(3L)).thenReturn(true);
        assertTrue(paymentService.exists(3L));
        verify(paymentRepository).existsById(3L);
    }

    /**
     * Verifies that {@link PaymentService#processPayment(Payment)} sets
     * timestamps and persists the payment.
     */
    @Test
    void processPayment_setsTimestampsAndSaves() {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.AUTHORIZED);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment saved = paymentService.processPayment(payment);

        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        verify(paymentRepository).save(saved);
    }

    /**
     * Verifies that {@link PaymentService#processPayment(Payment)} rejects
     * {@code null} input by throwing an {@link IllegalArgumentException}.
     */
    @Test
    void processPayment_nullPayment_throws() {
        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(null));
        verifyNoInteractions(paymentRepository);
    }

    /**
     * Verifies that {@link PaymentService#validatePaymentData(String, String, String)}
     * returns {@code true} for input that matches the expected formats.
     */
    @Test
    void validatePaymentData_validInput_returnsTrue() {
        boolean valid = paymentService.validatePaymentData("4532015112830366", "12/30", "123");
        assertTrue(valid);
    }

    /**
     * Verifies that {@link PaymentService#validatePaymentData(String, String, String)}
     * returns {@code false} for invalid card numbers, expiry formats, or CVV values.
     */
    @Test
    void validatePaymentData_invalidPatterns_returnFalse() {
        assertFalse(paymentService.validatePaymentData("", "12/30", "123"));
        assertFalse(paymentService.validatePaymentData("123", "12/30", "123"));
        assertFalse(paymentService.validatePaymentData("4532015112830366", "1230", "123"));
        assertFalse(paymentService.validatePaymentData("4532015112830366", "12/30", "12"));
    }

    /**
     * Verifies that {@link PaymentService#getPaymentCount()} delegates to
     * {@link PaymentRepository#count()}.
     */
    @Test
    void getPaymentCount_delegatesToRepository() {
        when(paymentRepository.count()).thenReturn(7L);
        assertEquals(7L, paymentService.getPaymentCount());
        verify(paymentRepository).count();
    }

    /**
     * Verifies that {@link PaymentService#getPaymentCountByStatus(PaymentStatus)}
     * counts only payments whose status matches the supplied value.
     */
    @Test
    void getPaymentCountByStatus_filtersByStatus() {
        Payment p1 = new Payment();
        p1.setStatus(PaymentStatus.AUTHORIZED);
        Payment p2 = new Payment();
        p2.setStatus(PaymentStatus.FAILED);
        Payment p3 = new Payment();
        p3.setStatus(PaymentStatus.AUTHORIZED);

        when(paymentRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        long successCount = paymentService.getPaymentCountByStatus(PaymentStatus.AUTHORIZED);
        assertEquals(2L, successCount);
    }
}