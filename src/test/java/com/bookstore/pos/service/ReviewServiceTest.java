package com.bookstore.pos.service;

import com.bookstore.demo.model.Book;
import com.bookstore.pos.model.Review;
import com.bookstore.pos.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReviewService}.
 * <p>
 * These tests verify retrieval of reviews for a book and the creation
 * of new reviews with the expected field values.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    /**
     * Verifies that {@link ReviewService#getReviewsForBook(Long)} delegates
     * to {@link ReviewRepository#findByBookId(Long)} with the same identifier.
     */
    @Test
    void getReviewsForBook_delegatesToRepository() {
        reviewService.getReviewsForBook(10L);
        verify(reviewRepository).findByBookId(10L);
    }

    /**
     * Verifies that {@link ReviewService#addReview(Book, String, String, int)}
     * builds a new {@link Review} entity with the correct properties and
     * passes it to {@link ReviewRepository#save(Object)}.
     */
    @Test
    void addReview_buildsAndSavesReview() {
        Book book = new Book();
        book.setTitle("Test Book");

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

        reviewService.addReview(book, "Alice", "Great read", 5);

        verify(reviewRepository).save(captor.capture());
        Review saved = captor.getValue();

        assertEquals("Alice", saved.getReviewerName());
        assertEquals("Great read", saved.getContent());
        assertEquals(5, saved.getRating());
        assertEquals(book, saved.getBook());
    }
}