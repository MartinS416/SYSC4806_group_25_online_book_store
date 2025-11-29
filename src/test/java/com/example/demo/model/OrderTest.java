package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void orderStartsWithDefaults() {
        System.out.println("=== orderStartsWithDefaults ===");
        Order order = new Order();

        System.out.println("Status: " + order.getStatus());
        System.out.println("Total Amount: " + order.getTotalAmount());
        System.out.println("Created At: " + order.getCreatedAt());

        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void addOrderLineUpdatesTotalAmount() {
        System.out.println("=== addOrderLineUpdatesTotalAmount ===");
        Order order = new Order();

        OrderLine line = new OrderLine();
        line.setPrice(new BigDecimal("10.00"));
        line.setQuantity(2);

        System.out.println("Adding line: price=10.00, qty=2");

        order.addOrderLine(line);

        System.out.println("Order lines: " + order.getOrderLines().size());
        System.out.println("Total after add: " + order.getTotalAmount());

        assertEquals(1, order.getOrderLines().size());
        assertEquals(new BigDecimal("20.00"), order.getTotalAmount());
    }

    @Test
    void removeOrderLineUpdatesTotalAmount() {
        System.out.println("=== removeOrderLineUpdatesTotalAmount ===");
        Order order = new Order();

        OrderLine line = new OrderLine();
        line.setPrice(new BigDecimal("5.00"));
        line.setQuantity(3);  // total = 15

        System.out.println("Adding line: price=5.00, qty=3");

        order.addOrderLine(line);
        System.out.println("Total after add: " + order.getTotalAmount());

        order.removeOrderLine(line);
        System.out.println("Removing line...");
        System.out.println("Total after remove: " + order.getTotalAmount());

        assertEquals(new BigDecimal("0.00"), order.getTotalAmount());
    }

}
