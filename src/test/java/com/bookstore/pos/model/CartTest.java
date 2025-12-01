package com.bookstore.pos.model;

import com.bookstore.demo.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Cart} entity in the POS domain.
 */
class CartTest {

    /**
     * Verifies that a new cart is associated with its customer and starts empty.
     */
    @Test
    void newCartStartsEmptyForCustomer() {
        Customer customer = new Customer();
        ReflectionTestUtils.setField(customer, "id", 42L);

        Cart cart = new Cart(customer);

        assertEquals(customer, cart.getCustomer());
        assertTrue(cart.getItems().isEmpty());
    }
}