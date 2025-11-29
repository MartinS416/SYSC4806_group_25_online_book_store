package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Review;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BookController {

    private final BookRepository bookRepository;
    private final ReviewService reviewService;

    public BookController(BookRepository bookRepository, ReviewService reviewService) {
        this.bookRepository = bookRepository;
        this.reviewService = reviewService;
    }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable Long id, Model model) {

        Book book = bookRepository.findById(id).orElse(null);
        List<Review> reviews = reviewService.getReviewsForBook(id);

        double averageRating = reviews.isEmpty() ? 0 :
                reviews.stream().mapToInt(Review::getRating).average().orElse(0);

        long[] breakdown = new long[5];
        reviews.forEach(r -> breakdown[5 - r.getRating()]++);

        model.addAttribute("book", book);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("breakdown", breakdown);

        return "book-details";
    }


