package com.bookstore.pos.model;

import com.bookstore.demo.model.Book;
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


    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal;

    public OrderLine() {}

    public OrderLine(Order order, Book book, int quantity, BigDecimal price) {
        this.order = order;
        this.book = book;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    // GETTERS
    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getSubtotal() { return subtotal; }

    // SETTERS
    public void setOrder(Order order) { this.order = order; }
    public void setBook(Book book) { this.book = book; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.price != null) {
            this.subtotal = this.price.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        if (this.quantity > 0) {
            this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
