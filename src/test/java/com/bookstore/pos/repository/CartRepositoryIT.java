package com.bookstore.pos.repository;

import com.bookstore.common.model.Customer;
import com.bookstore.pos.model.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link CartRepository}.
 * <p>
 * Level: integration (Spring Data JPA + H2).
 * Verifies persistence and custom queries used in POS cart flows.
 */
@DataJpaTest
class CartRepositoryIT {

    @Autowired
    private CartRepository cartRepository;

    /**
     * Verifies that {@link CartRepository#findByCustomerIdAndActiveTrue(Long)}
     * returns the active cart for a given customer.
     */
    @Test
    void findByCustomerIdAndActiveTrue_returnsActiveCart() {
        Customer customer = new Customer();
        ReflectionTestUtils.setField(customer, "id", 42L);

        Cart cart = new Cart(customer);
        cart.activate();

        Cart saved = cartRepository.save(cart);

        Optional<Cart> found = cartRepository.findByCustomerIdAndActiveTrue(42L);

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(42L, found.get().getCustomer().getId());
    }
}