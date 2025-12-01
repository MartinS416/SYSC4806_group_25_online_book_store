package com.bookstore.pos.repository;

import com.bookstore.pos.model.Review;
import com.bookstore.inventory.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ReviewRepository}.
 * <p>
 * Level: integration.
 * Verifies that a book id can retrieve reviews as required by POS flows.
 */
@DataJpaTest
class ReviewRepositoryIT {

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Verifies that {@link ReviewRepository#findByBookId(Long)} returns reviews
     * associated with a specific book.
     */
    @Test
    void findByBookId_returnsMatchingReviews() {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 10L);


        Review review = new Review();
        review.setBook(book);
        review.setRating(4);
        review.setContent("Great book!");
        reviewRepository.save(review);

        List<Review> reviews = reviewRepository.findByBookId(10L);
        assertEquals(1, reviews.size());
        assertEquals(4, reviews.getFirst().getRating());
    }
}