package com.bookstore.inventory.controller;

import com.bookstore.inventory.model.Book;
import com.bookstore.pos.model.Review;
import com.bookstore.inventory.repository.BookRepository;
import com.bookstore.pos.service.ReviewService;
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
        if (book == null) {
            return "redirect:/shop";   // safety fallback
        }

        List<Review> reviews = reviewService.getReviewsForBook(id);

        // Average rating
        double averageRating = reviews.isEmpty() ? 0 :
                reviews.stream().mapToInt(Review::getRating).average().orElse(0);

        // Rating breakdown (5★ to 1★)
        long[] breakdown = new long[5];
        reviews.forEach(r -> breakdown[5 - r.getRating()]++);

        // ★★★ ADD THIS — This fixes the 500 error ★★★
        double[] barWidth = new double[5];
        int totalReviews = reviews.size();

        if (totalReviews > 0) {
            for (int i = 0; i < 5; i++) {
                barWidth[i] = (breakdown[i] * 100.0) / totalReviews;
            }
        }

        model.addAttribute("book", book);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("breakdown", breakdown);
        model.addAttribute("barWidth", barWidth);  // ★ REQUIRED BY TEMPLATE ★

        return "book-details";
    }

    @PostMapping("/books/{id}/reviews")
    public String addReview(@PathVariable Long id,
                            @RequestParam String reviewerName,
                            @RequestParam String content,
                            @RequestParam int rating) {

        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return "redirect:/shop";
        }

        reviewService.addReview(book, reviewerName, content, rating);

        return "redirect:/books/" + id;
    }
}
