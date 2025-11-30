package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT CAST(o.createdAt AS date), SUM(o.totalAmount)
        FROM Order o
        GROUP BY CAST(o.createdAt AS date)
        ORDER BY CAST(o.createdAt AS date)
    """)
    List<Object[]> getDailyRevenue();

    @Query("""
        SELECT CAST(o.createdAt AS date), COUNT(o)
        FROM Order o
        GROUP BY CAST(o.createdAt AS date)
        ORDER BY CAST(o.createdAt AS date)
    """)
    List<Object[]> getOrdersPerDay();

    List<Order> findAllByOrderByCreatedAtDesc();
}
