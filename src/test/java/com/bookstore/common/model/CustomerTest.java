package com.bookstore.common.model;

import com.bookstore.pos.model.Cart;
import com.bookstore.pos.model.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

class CustomerTest {

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

    @Test
    void testAddAndRemoveAddress() {
        Customer customer = new Customer();
        Address mockAddress = Mockito.mock(Address.class);

        customer.addAddress(mockAddress);
        assertTrue(customer.getAddresses().contains(mockAddress));

        customer.removeAddress(mockAddress);
        assertFalse(customer.getAddresses().contains(mockAddress));
    }

    @Test
    void testAddAndRemoveCart() {
        Customer customer = new Customer();
        Cart mockCart = Mockito.mock(Cart.class);

        customer.addCart(mockCart);
        assertTrue(customer.getCarts().contains(mockCart));

        customer.removeCart(mockCart);
        assertFalse(customer.getCarts().contains(mockCart));
    }

    @Test
    void testAddOrderInfo() {
        Customer customer = new Customer();
        Order mockOrder = Mockito.mock(Order.class);

        customer.addOrderInfo(mockOrder);
        assertTrue(customer.getOrderInfos().contains(mockOrder));
    }
}
