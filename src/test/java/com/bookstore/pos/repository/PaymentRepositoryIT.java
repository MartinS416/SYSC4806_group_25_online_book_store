package com.bookstore.pos.repository;

import com.bookstore.demo.model.Address;
import com.bookstore.demo.model.Customer;
import com.bookstore.demo.repository.AddressRepository;
import com.bookstore.demo.repository.CustomerRepository;
import com.bookstore.pos.model.Order;
import com.bookstore.pos.model.OrderStatus;
import com.bookstore.pos.model.Payment;
import com.bookstore.pos.model.PaymentStatus;
import org.checkerframework.checker.units.qual.A;
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    /**
     * Verifies that a payment can be persisted and retrieved by id.
     */
    @Test
    void saveAndFindById_persistsPayment() {
        Payment p = new Payment();
        p.setAmount(BigDecimal.TEN);
        p.setStatus(PaymentStatus.PENDING);

        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setTotalAmount(new BigDecimal("5.00"));

        Customer customer = new Customer();
        customerRepository.save(customer);

        Address address = new Address();
        address.setCustomer(customer);
        address.setFirstName("john");
        address.setLastName("johnson");
        address.setCity("ottawa");
        address.setStreet("123 street");
        address.setRegion("ont");
        address.setPostcode("A1A 1A1");

        addressRepository.save(address);

        order.setAddress(address);
        order.setCustomer(customer);

        orderRepository.save(order);

        p.setOrder(order);

        Payment saved = paymentRepository.save(p);

        Optional<Payment> found = paymentRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(BigDecimal.TEN, found.get().getAmount());
        assertEquals(PaymentStatus.PENDING, found.get().getStatus());
    }
}