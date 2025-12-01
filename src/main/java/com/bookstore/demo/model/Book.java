package com.bookstore.demo.model;

import com.bookstore.pos.model.Review;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private BigDecimal price;
    private String category;
    private int stock;


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public Book() {}

    public Book(String title, String author, BigDecimal price, String category, int stock) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public int getStock() { return stock; }


    public List<Review> getReviews() { return reviews; }


    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setStock(int stock) { this.stock = stock; }

    
    public void addReview(Review review) {
        reviews.add(review);
        review.setBook(this);
    }
}
