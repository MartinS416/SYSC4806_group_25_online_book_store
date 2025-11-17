package com.example.demo.service;

import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.example.demo.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment create(Payment payment) { return paymentRepository.save(payment); }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
    }

    public List<Payment> findAll() { return paymentRepository.findAll(); }

    public Payment updateStatus(Long id, PaymentStatus status) {
        Payment p = findById(id);
        p.setStatus(status);
        p.setUpdatedAt(java.time.Instant.now());
        return paymentRepository.save(p);
    }

    public void delete(Long id) { paymentRepository.deleteById(id); }
}
