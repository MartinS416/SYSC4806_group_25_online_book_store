package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Payment;
import com.example.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
}
