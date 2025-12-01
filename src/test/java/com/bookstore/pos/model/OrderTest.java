package com.bookstore.pos.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Order} entity.
 */
class OrderTest {

    /**
     * Verifies that a new order starts with default status, zero total, and a creation timestamp.
     */
    @Test
    void orderStartsWithDefaults() {
        Order order = new Order();

        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertNotNull(order.getCreatedAt());
    }

    /**
     * Verifies that adding an order line updates the total amount.
     */
    @Test
    void addOrderLineUpdatesTotalAmount() {
        Order order = new Order();

        OrderLine line = new OrderLine();
        line.setPrice(new BigDecimal("10.00"));
        line.setQuantity(2);

        order.addOrderLine(line);

        assertEquals(1, order.getOrderLines().size());
        assertEquals(new BigDecimal("20.00"), order.getTotalAmount());
    }

    /**
     * Verifies that removing an order line subtracts its subtotal from the total amount.
     */
    @Test
    void removeOrderLineUpdatesTotalAmount() {
        Order order = new Order();

        OrderLine line = new OrderLine();
        line.setPrice(new BigDecimal("5.00"));
        line.setQuantity(3);  // total = 15

        order.addOrderLine(line);
        order.removeOrderLine(line);

        assertEquals(new BigDecimal("0.00"), order.getTotalAmount());
    }
}