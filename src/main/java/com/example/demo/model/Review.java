package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reviewerName;

    @Column(length = 2000)
    private String content;

    private int rating; // 1–5 stars

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    public Review() {}

    public Review(String reviewerName, String content, int rating, Book book) {
        this.reviewerName = reviewerName;
        this.content = content;
        this.rating = rating;
        this.book = book;
    }

    public Long getId() { return id; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
}
