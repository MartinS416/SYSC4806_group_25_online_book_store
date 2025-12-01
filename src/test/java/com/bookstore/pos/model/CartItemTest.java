package com.bookstore.pos.model;

import com.bookstore.inventory.model.Book;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CartItem} in the POS domain.
 * <p>
 * These tests verify that a cart item correctly associates a book and quantity,
 * and that any derived values (such as line totals, if present) behave as expected.
 */
class CartItemTest {

    /**
     * Verifies that a new cart item can be created with a book and quantity.
     */
    @Test
    void canCreateCartItemWithBookAndQuantity() {
        Book book = new Book();
        book.setId(1L);
        book.setPrice(BigDecimal.valueOf(9.99));

        CartItem item = new CartItem();
        item.setBook(book);
        item.setQuantity(3);

        assertEquals(book, item.getBook());
        assertEquals(3, item.getQuantity());
    }

    /**
     * Verifies that a cart item can be linked to a {@link Cart}.
     */
    @Test
    void canAssociateWithCart() {
        Cart cart = new Cart(null);
        CartItem item = new CartItem();

        item.setCart(cart);

        assertEquals(cart, item.getCart());
    }

    /**
     * Verifies that a derived subtotal (if implemented) is price × quantity.
     */
    @Test
    void subtotalIsPriceTimesQuantity() {
        Book book = new Book();
        book.setPrice(new BigDecimal("5.00"));

        CartItem item = new CartItem();
        item.setBook(book);
        item.setQuantity(4);

        // Uncomment if CartItem exposes a subtotal-style method:
        assertEquals(new BigDecimal("20.00"), item.getSubtotal());
    }
}