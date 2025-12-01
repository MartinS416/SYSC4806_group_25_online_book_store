package com.bookstore.common.model;

import com.bookstore.pos.model.Cart;
import com.bookstore.pos.model.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Customer}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Domain model.
 * <h2>Scope:</h2>
 * <ul>
 *   <li>Constructor and getter behavior for core identity/contact fields.</li>
 *   <li>Initialization of relationship collections (orders, carts, addresses).</li>
 *   <li>Helper methods for managing associated addresses, carts, and orders.</li>
 * </ul>
 *
 * <h2>Dependencies:</h2> {@link Cart}, {@link Order}, {@link Address} (mocked for association tests).
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-11-02
 */
class CustomerTest {

    /**
     * Test: constructor sets core fields and initializes collections.
     */
    @Test
    void testConstructorAndGetters() {
        Customer customer = new Customer(
                "john_doe",
                "secret123",
                "john@example.com",
                "John",
                "Doe",
                "555-1234"
        );

        assertEquals("john_doe", customer.getUsername());
        assertEquals("secret123", customer.getPassword());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("555-1234", customer.getPhone());
        assertNotNull(customer.getOrderInfos());
        assertNotNull(customer.getCarts());
        assertNotNull(customer.getAddresses());
    }

    /**
     * Test: addAddress and removeAddress maintain the addresses collection.
     */
    @Test
    void testAddAndRemoveAddress() {
        Customer customer = new Customer();
        Address mockAddress = Mockito.mock(Address.class);

        customer.addAddress(mockAddress);
        assertTrue(customer.getAddresses().contains(mockAddress));

        customer.removeAddress(mockAddress);
        assertFalse(customer.getAddresses().contains(mockAddress));
    }

    /**
     * Test: addCart and removeCart maintain the carts collection.
     */
    @Test
    void testAddAndRemoveCart() {
        Customer customer = new Customer();
        Cart mockCart = Mockito.mock(Cart.class);

        customer.addCart(mockCart);
        assertTrue(customer.getCarts().contains(mockCart));

        customer.removeCart(mockCart);
        assertFalse(customer.getCarts().contains(mockCart));
    }

    /**
     * Test: addOrderInfo appends orders to the orderInfos collection.
     */
    @Test
    void testAddOrderInfo() {
        Customer customer = new Customer();
        Order mockOrder = Mockito.mock(Order.class);

        customer.addOrderInfo(mockOrder);

        assertTrue(customer.getOrderInfos().contains(mockOrder));
    }
}