package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderLineTest {

    @Test
    void constructorComputesSubtotal() {
        System.out.println("=== constructorComputesSubtotal ===");

        OrderLine line = new OrderLine(null, null, 3, new BigDecimal("5.00"));

        System.out.println("Quantity: " + line.getQuantity());
        System.out.println("Price: " + line.getPrice());
        System.out.println("Subtotal: " + line.getSubtotal());

        assertEquals(new BigDecimal("15.00"), line.getSubtotal());
    }

    @Test
    void setQuantityUpdatesSubtotal() {
        System.out.println("=== setQuantityUpdatesSubtotal ===");

        OrderLine line = new OrderLine();
        line.setPrice(new BigDecimal("10.00"));
        line.setQuantity(2);

        System.out.println("After setQuantity -> Subtotal: " + line.getSubtotal());

        assertEquals(new BigDecimal("20.00"), line.getSubtotal());
    }

    @Test
    void setPriceUpdatesSubtotal() {
        System.out.println("=== setPriceUpdatesSubtotal ===");

        OrderLine line = new OrderLine();
        line.setQuantity(4);
        line.setPrice(new BigDecimal("3.50"));

        System.out.println("After setPrice -> Subtotal: " + line.getSubtotal());

        assertEquals(new BigDecimal("14.00"), line.getSubtotal());
    }

    @Test
    void canSetRelations() {
        System.out.println("=== canSetRelations ===");

        Order order = new Order();
        Book book = new Book();

        OrderLine line = new OrderLine();
        line.setOrder(order);
        line.setBook(book);

        System.out.println("Order set? " + (line.getOrder() != null));
        System.out.println("Book set? " + (line.getBook() != null));

        assertEquals(order, line.getOrder());
        assertEquals(book, line.getBook());
    }
}
