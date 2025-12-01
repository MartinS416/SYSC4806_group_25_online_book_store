package com.bookstore.pos.service;

import com.bookstore.pos.model.Payment;
import com.bookstore.pos.model.PaymentStatus;
import com.bookstore.pos.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service for persisting and managing payment records.
 * <p>
 * This service updates {@link Payment} entities and their status. It does not
 * directly integrate with external payment gateways.
 */
@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * Creates a new PaymentService with the given repository.
     *
     * @param paymentRepository repository for {@link Payment} entities
     */
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Creates and persists a new payment with an initial PENDING status.
     *
     * @param payment the payment to create (must not be {@code null})
     * @return the saved payment including its generated identifier
     * @throws IllegalArgumentException if {@code payment} is {@code null}
     */
    @Transactional
    public Payment create(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        payment.setCreatedAt(Instant.now());
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    /**
     * Retrieves a payment by its identifier.
     *
     * @param id the identifier of the payment
     * @return the matching payment
     * @throws IllegalArgumentException if no payment exists with the given id
     */
    @Transactional(readOnly = true)
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
    }

    /**
     * Retrieves a payment by its identifier as an {@link Optional}.
     *
     * @param id the identifier of the payment
     * @return an optional containing the payment if found, or empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<Payment> findByIdOptional(Long id) {
        return paymentRepository.findById(id);
    }

    /**
     * Returns all persisted payments.
     *
     * @return a list of all payments
     */
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    /**
     * Updates the status of an existing payment.
     *
     * @param id     the identifier of the payment to update
     * @param status the new payment status
     * @return the updated payment
     * @throws IllegalArgumentException if no payment exists with the given id
     */
    @Transactional
    public Payment updateStatus(Long id, PaymentStatus status) {
        Payment p = findById(id);
        p.setStatus(status);
        p.setUpdatedAt(Instant.now());
        return paymentRepository.save(p);
    }

    /**
     * Marks the specified payment as successful.
     *
     * @param id the identifier of the payment to mark as successful
     * @return the updated payment
     */
    @Transactional
    public Payment markSuccessful(Long id) {
        return updateStatus(id, PaymentStatus.AUTHORIZED);
    }

    /**
     * Marks the specified payment as failed.
     * <p>
     * The current implementation stores only the status; if a failure reason
     * field is added to {@link Payment}, it can be populated here.
     *
     * @param id     the identifier of the payment to mark as failed
     * @param reason an optional human-readable failure reason
     * @return the updated payment
     */
    @Transactional
    public Payment markFailed(Long id, String reason) {
        Payment p = updateStatus(id, PaymentStatus.FAILED);
        // If Payment has a failureReason field, populate it here.
        return p;
    }

    /**
     * Deletes a payment by its identifier.
     *
     * @param id the identifier of the payment to delete
     * @throws IllegalArgumentException if no payment exists with the given id
     */
    @Transactional
    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new IllegalArgumentException("Payment not found: " + id);
        }
        paymentRepository.deleteById(id);
    }

    /**
     * Checks whether a payment with the given identifier exists.
     *
     * @param id the identifier of the payment
     * @return {@code true} if a payment exists; {@code false} otherwise
     */
    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return paymentRepository.existsById(id);
    }

    /**
     * Processes a payment record by setting timestamps and persisting it.
     * <p>
     * This method assumes the business logic has already determined the
     * payment {@link PaymentStatus} (for example, SUCCESS or FAILED).
     *
     * @param payment the payment to process (must not be {@code null})
     * @return the processed and saved payment
     * @throws IllegalArgumentException if {@code payment} is {@code null}
     */
    @Transactional
    public Payment processPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(Instant.now());
        }
        payment.setUpdatedAt(Instant.now());

        Payment saved = paymentRepository.save(payment);
        System.out.println("Payment processed: ID=" + saved.getId() + ", Status=" + saved.getStatus());
        return saved;
    }

    /**
     * Performs basic format validation on card data.
     * <p>
     * The current implementation checks that the card number has 16 digits,
     * the expiry matches {@code MM/yy}, and the CVV has 3–4 digits.
     *
     * @param cardNumber the card number to validate
     * @param expiry     the expiry in {@code MM/yy} format
     * @param cvv        the CVV to validate
     * @return {@code true} if all fields match the expected format; {@code false} otherwise
     */
    public boolean validatePaymentData(String cardNumber, String expiry, String cvv) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }
        if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            return false;
        }
        if (!cardNumber.matches("\\d{16}")) {
            return false;
        }
        return true;
    }

    /**
     * Returns the total number of payments in the system.
     *
     * @return the number of persisted payments
     */
    @Transactional(readOnly = true)
    public long getPaymentCount() {
        return paymentRepository.count();
    }

    /**
     * Returns the number of payments that have a given status.
     *
     * @param status the status to filter by
     * @return the count of payments with the specified status
     */
    @Transactional(readOnly = true)
    public long getPaymentCountByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .filter(p -> p.getStatus() == status)
                .count();
    }
}
