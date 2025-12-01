package com.bookstore.pos.service;

import com.bookstore.demo.model.Book;
import com.bookstore.pos.model.Review;
import com.bookstore.pos.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewsForBook(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    public void addReview(Book book, String reviewerName, String content, int rating) {
        Review review = new Review();
        review.setReviewerName(reviewerName);
        review.setContent(content);
        review.setRating(rating);
        review.setBook(book);

        reviewRepository.save(review);
    }
}
