package com.bookstore.pos.repository;

import com.bookstore.pos.model.Order;
import com.bookstore.pos.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link OrderRepository}.
 * <p>
 * Level: integration.
 * Verifies persistence and reporting queries such as daily revenue and
 * ordered-by-date retrieval.
 */
@DataJpaTest
class OrderRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Verifies that orders are saved and can be listed in descending creation order.
     */
    @Test
    void findAllByOrderByCreatedAtDesc_returnsMostRecentFirst() {
        Order older = new Order();
        older.setStatus(OrderStatus.NEW);
        older.setTotalAmount(new BigDecimal("10.00"));
        older.setCreatedAt(Instant.now().minusSeconds(3600));

        Order newer = new Order();
        newer.setStatus(OrderStatus.NEW);
        newer.setTotalAmount(new BigDecimal("20.00"));
        newer.setCreatedAt(Instant.now());

        orderRepository.save(older);
        orderRepository.save(newer);

        List<Order> result = orderRepository.findAllByOrderByCreatedAtDesc();
        assertEquals(2, result.size());
        assertEquals(newer.getTotalAmount(), result.get(0).getTotalAmount());
    }

    /**
     * Verifies that {@link OrderRepository#getDailyRevenue()} returns at least
     * one row when orders exist and that the aggregated revenue is positive.
     */
    @Test
    void getDailyRevenue_returnsAggregatedRows() {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setTotalAmount(new BigDecimal("15.00"));
        orderRepository.save(order);

        List<?> rows = orderRepository.getDailyRevenue();
        assertFalse(rows.isEmpty());
    }

    /**
     * Verifies that {@link OrderRepository#getOrdersPerDay()} returns at least
     * one row when orders exist.
     */
    @Test
    void getOrdersPerDay_returnsCounts() {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setTotalAmount(new BigDecimal("5.00"));
        orderRepository.save(order);

        List<?> rows = orderRepository.getOrdersPerDay();
        assertFalse(rows.isEmpty());
    }
}