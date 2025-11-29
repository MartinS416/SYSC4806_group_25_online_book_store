package com.example.demo.repository;

import com.example.demo.model.OrderLine;
import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * Repository for OrderLine entities.
 */
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
    List<OrderLine> findByOrder(Order order);
    List<OrderLine> findByOrderId(Long orderId);

    // Top-selling books
    @Query("""
        SELECT ol.book.title, SUM(ol.quantity)
        FROM OrderLine ol
        GROUP BY ol.book.id
        ORDER BY SUM(ol.quantity) DESC
    """)
    List<Object[]> getTopSellingBooks();

    // Revenue by category
    @Query("""
        SELECT ol.book.category, SUM(ol.subtotal)
        FROM OrderLine ol
        GROUP BY ol.book.category
        ORDER BY SUM(ol.subtotal) DESC
    """)
    List<Object[]> getRevenueByCategory();

}