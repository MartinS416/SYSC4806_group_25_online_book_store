package com.bookstore.pos.model;

import com.bookstore.inventory.model.Book;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OrderLine} entity.
 */
class OrderLineTest {

    /**
     * Verifies that the constructor computes the subtotal as price × quantity.
     */
    @Test
    void constructorComputesSubtotal() {
        OrderLine line = new OrderLine(null, null, 3, new BigDecimal("5.00"));
        assertEquals(new BigDecimal("15.00"), line.getSubtotal());
    }

    /**
     * Verifies that changing the quantity updates the subtotal.
     */
    @Test
    void setQuantityUpdatesSubtotal() {
        OrderLine line = new OrderLine();
        line.setPrice(new BigDecimal("10.00"));
        line.setQuantity(2);
        assertEquals(new BigDecimal("20.00"), line.getSubtotal());
    }

    /**
     * Verifies that changing the price updates the subtotal.
     */
    @Test
    void setPriceUpdatesSubtotal() {
        OrderLine line = new OrderLine();
        line.setQuantity(4);
        line.setPrice(new BigDecimal("3.50"));
        assertEquals(new BigDecimal("14.00"), line.getSubtotal());
    }

    /**
     * Verifies that order and book relations can be set on the order line.
     */
    @Test
    void canSetRelations() {
        Order order = new Order();
        Book book = new Book();

        OrderLine line = new OrderLine();
        line.setOrder(order);
        line.setBook(book);

        assertEquals(order, line.getOrder());
        assertEquals(book, line.getBook());
    }
}