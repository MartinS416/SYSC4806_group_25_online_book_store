package com.bookstore.inventory.controller;

import com.bookstore.inventory.model.Book;
import com.bookstore.pos.model.Review;
import com.bookstore.inventory.repository.BookRepository;
import com.bookstore.pos.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BookController}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Web/controller layer.
 * <h2>Scope:</h2> Book detail view, review aggregation, and review submission.
 * <h2>Dependencies:</h2> {@link BookRepository}, {@link ReviewService} (mocked).
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookController Unit Tests")
class BookControllerTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private BookController controller;

    @Test
    @DisplayName("viewBook redirects to /shop when book not found")
    void viewBook_notFound_redirectsToShop() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Model model = new ExtendedModelMap();

        String view = controller.viewBook(1L, model);

        assertEquals("redirect:/shop", view);
    }

    @Test
    @DisplayName("viewBook populates model with book, reviews, average rating and breakdown")
    void viewBook_populatesModel() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Review r1 = new Review();
        r1.setRating(5);
        Review r2 = new Review();
        r2.setRating(3);
        when(reviewService.getReviewsForBook(1L)).thenReturn(List.of(r1, r2));

        Model model = new ExtendedModelMap();

        String view = controller.viewBook(1L, model);

        assertEquals("book-details", view);
        assertSame(book, model.getAttribute("book"));
        assertEquals(List.of(r1, r2), model.getAttribute("reviews"));
        assertNotNull(model.getAttribute("averageRating"));
        assertNotNull(model.getAttribute("breakdown"));
    }

    @Test
    @DisplayName("addReview redirects to /shop when book not found")
    void addReview_notFound_redirectsToShop() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        String view = controller.addReview(1L, "Alice", "Nice book", 5);

        assertEquals("redirect:/shop", view);
        verifyNoInteractions(reviewService);
    }

    @Test
    @DisplayName("addReview delegates to ReviewService and redirects back to book")
    void addReview_savesReviewAndRedirects() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        String view = controller.addReview(1L, "Alice", "Nice book", 5);

        assertEquals("redirect:/books/1", view);
        verify(reviewService).addReview(book, "Alice", "Nice book", 5);
    }
}