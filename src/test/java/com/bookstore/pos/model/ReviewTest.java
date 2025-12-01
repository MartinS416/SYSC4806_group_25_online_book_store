package com.bookstore.pos.model;

import com.bookstore.inventory.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Review} in the POS domain.
 * <p>
 * These tests focus on basic field assignment and, if applicable,
 * any rating bounds or validation enforced at the entity level.
 */
class ReviewTest {

    /**
     * Verifies that basic fields such as rating and text can be set and retrieved.
     */
    @Test
    void canSetRatingAndText() {
        Review review = new Review();
        review.setRating(4);
        review.setContent("Great book!");

        assertEquals(4, review.getRating());
        assertEquals("Great book!", review.getContent());
    }

    /**
     * Verifies that a review can be associated with a book and a customer.
     */
    @Test
    void canAssociateWithBookAndCustomer() {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);

        String customer = "New User";

        Review review = new Review();
        ReflectionTestUtils.setField(review, "id", 100L);
        review.setBook(book);
        review.setReviewerName(customer);
        review.setRating(5);
        review.setContent("Great book!");

        assertEquals(book, review.getBook());
        assertEquals(customer, review.getReviewerName());
    }

    /**
     * Verifies that rating values within the expected range are accepted.
     * <p>
     * If rating validation is implemented with annotations or explicit checks
     * in setters, this test can be extended to assert constraint violations.
     */
    @Test
    void ratingWithinExpectedRange() {
        Review review = new Review();
        review.setRating(1);
        assertEquals(1, review.getRating());

        review.setRating(5);
        assertEquals(5, review.getRating());
    }
}