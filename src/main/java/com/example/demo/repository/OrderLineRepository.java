package com.example.demo.repository;

import com.example.demo.model.OrderLine;
import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for OrderLine entities.
 */
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
    List<OrderLine> findByOrder(Order order);
    List<OrderLine> findByOrderId(Long orderId);
}