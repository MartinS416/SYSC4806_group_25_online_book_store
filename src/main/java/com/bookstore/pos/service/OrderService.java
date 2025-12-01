package com.bookstore.pos.service;

import com.bookstore.demo.model.*;
import com.bookstore.pos.model.*;
import com.bookstore.pos.repository.OrderLineRepository;
import com.bookstore.pos.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;

    public OrderService(OrderRepository orderRepository, OrderLineRepository orderLineRepository) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
    }

    public List<Order> getRecentOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Order create(Order order) { return orderRepository.save(order); }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    public List<Order> findAll() { return orderRepository.findAll(); }

    public Order updateStatus(Long id, OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order addPayment(Long id, Payment payment) {
        Order order = findById(id);
        order.setPayment(payment);
        return orderRepository.save(order);
    }

    public void delete(Long id) { orderRepository.deleteById(id); }

    public Order createOrder(Customer customer, Address address, List<CartItem> items) {
    Order order = new Order();
    order.setCustomer(customer);
    order.setAddress(address);
    order.setName(customer.getFirstName() + " " + customer.getLastName());
    order.setEmail(customer.getEmail());
    order.setPhone(customer.getPhone());
    
    BigDecimal total = BigDecimal.ZERO;
    
    for (CartItem item : items) {
        OrderLine line = new OrderLine();
        line.setOrder(order);
        line.setBook(item.getBook());
        line.setQuantity(item.getQuantity());
        
        // Calculate line subtotal: price × quantity
        BigDecimal linePrice = item.getBook().getPrice()
            .multiply(new BigDecimal(item.getQuantity()));
        total = total.add(linePrice);
        
        order.getOrderLines().add(line);
    }
    
    order.setTotalAmount(total);
    
    return orderRepository.save(order);
    }

    public List<Map<String, Object>> getDailyRevenue() {
        List<Object[]> rows = orderRepository.getDailyRevenue();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] r : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", r[0].toString());
            map.put("revenue", r[1]);
            result.add(map);
        }
        System.out.println(result);
        return result;
    }

    public List<Map<String, Object>> getTopSellingBooks() {
        List<Object[]> rows = orderLineRepository.getTopSellingBooks();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] r : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", r[0]);
            map.put("units", r[1]);
            result.add(map);
        }

        return result;
    }

    public List<Map<String, Object>> getRevenueByCategory() {
        List<Object[]> rows = orderLineRepository.getRevenueByCategory();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] r : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", r[0]);
            map.put("revenue", r[1]);
            result.add(map);
        }

        return result;
    }
}
