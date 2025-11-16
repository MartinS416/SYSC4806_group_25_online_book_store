package com.example.demo.model;

import com.example.demo.Book;
import jakarta.persistence.*;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // book being added
    @ManyToOne(optional = false)
    private Book book;

    // the owning cart
    @ManyToOne(optional = false)
    private Cart cart;

    private int quantity = 1;

    // constructors
    public CartItem() {}

    public CartItem(Book book, Cart cart) {
        this.book = book;
        this.cart = cart;
    }

    // getters/setters
    public Long getId() { return id; }
    public Book getBook() { return book; }
    public Cart getCart() { return cart; }
    public int getQuantity() { return quantity; }

    public void setBook(Book book) { this.book = book; }
    public void setCart(Cart cart) { this.cart = cart; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
