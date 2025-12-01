package com.bookstore.pos.model;

import com.bookstore.demo.model.Book;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private int quantity = 1;

    public CartItem() {}

    public CartItem(Cart cart, Book book, int quantity) {
        this.cart = cart;
        this.book = book;
        this.quantity = quantity;
    }

    // GETTERS //
    public Long getId() { return id; }
    public Cart getCart() { return cart; }
    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return book.getPrice().multiply(BigDecimal.valueOf(quantity)); }

    // SETTERS //
    public void setCart(Cart cart) { this.cart = cart; }
    public void setBook(Book book) { this.book = book; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void increment(int delta) { this.quantity = Math.max(0, this.quantity + delta); }
}