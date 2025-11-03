package com.example.demo;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private double price;
    private String category;

    public Book() {}

    public Book(String title, String author, double price, String category) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.category = category;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
}
