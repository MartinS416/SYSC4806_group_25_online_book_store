package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private int quantity;
    private BigDecimal price;

    public OrderLine() {}

    public OrderLine(Order order, Book book, int quantity, BigDecimal price) {
        this.order = order;
        this.book = book;
        this.quantity = quantity;
        this.price = price;
    }

    // GETTERS //
    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }

    // SETTERS //
    public void setOrder(Order order) { this.order = order; }
    public void setBook(Book book) { this.book = book; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSubtotal(BigDecimal price) { this.price = price; }
}