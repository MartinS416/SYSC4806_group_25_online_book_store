package com.bookstore.inventory.service;

import com.bookstore.inventory.model.Book;
import com.bookstore.inventory.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for {@link BookService}.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Type:</strong> Integration Test (IT)</li>
 *   <li><strong>Layer:</strong> Service (business) layer and persistence</li>
 *   <li><strong>Scope:</strong> CRUD operations, search, filter, and category retrieval for {@link Book}</li>
 *   <li><strong>Dependencies:</strong> {@link BookRepository}, Spring Data JPA, H2 in-memory database</li>
 *   <li><strong>Framework:</strong> JUnit 5, AssertJ, {@link DataJpaTest}</li>
 * </ul>
 *
 * <h2>Objectives</h2>
 * <ul>
 *   <li>Verify that service methods delegate correctly to {@link BookRepository}.</li>
 *   <li>Ensure exceptions are thrown for missing entities.</li>
 *   <li>Validate business-facing operations (searchBooks, filterBooks, findAllCategories).</li>
 * </ul>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DataJpaTest
@Import(BookService.class)
@DisplayName("BookService Integration Tests")
class BookServiceIT {

    @Autowired
    private BookService bookService;

    /**
     * Helper to build and persist a {@link Book} via the service.
     */
    private Book createBook(String title, String author, String category, double price, int stock) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setCategory(category);
        b.setPrice(BigDecimal.valueOf(price));
        b.setStock(stock);
        return bookService.create(b);
    }

    @Nested
    @DisplayName("CRUD operations")
    class CrudTests {

        /**
         * Test: Should create and find a book by id.
         */
        @Test
        @DisplayName("Should create and find book by id")
        void createAndFindById() {
            Book saved = createBook("Spring in Action", "Craig Walls", "Programming", 40, 5);

            Book found = bookService.findById(saved.getId());

            assertThat(found.getTitle()).isEqualTo("Spring in Action");
            assertThat(found.getAuthor()).isEqualTo("Craig Walls");
        }

        /**
         * Test: Should throw when book id does not exist.
         */
        @Test
        @DisplayName("Should throw when book not found")
        void findById_notFound() {
            assertThatThrownBy(() -> bookService.findById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Book not found");
        }

        /**
         * Test: Should update existing book fields.
         */
        @Test
        @DisplayName("Should update existing book")
        void updateBook() {
            Book saved = createBook("Old Title", "Author", "Programming", 30, 3);

            Book updated = new Book();
            updated.setTitle("New Title");
            updated.setAuthor("New Author");
            updated.setCategory("New Category");
            updated.setPrice(BigDecimal.valueOf(50));
            updated.setStock(10);

            Book result = bookService.update(saved.getId(), updated);

            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getAuthor()).isEqualTo("New Author");
            assertThat(result.getCategory()).isEqualTo("New Category");
            assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(50));
            assertThat(result.getStock()).isEqualTo(10);
        }

        /**
         * Test: Should delete a book.
         */
        @Test
        @DisplayName("Should delete book")
        void deleteBook() {
            Book saved = createBook("To Delete", "Author", "Category", 10, 1);

            bookService.delete(saved.getId());

            assertThatThrownBy(() -> bookService.findById(saved.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Search and filter operations")
    class SearchAndFilterTests {

        /**
         * Test: Should search books by keyword via service.
         */
        @Test
        @DisplayName("Should search books by keyword")
        void searchBooks() {
            createBook("Spring in Action", "Craig Walls", "Programming", 40, 5);
            createBook("Cooking 101", "Chef John", "Cooking", 20, 2);

            List<Book> results = bookService.searchBooks("spring");

            assertThat(results)
                    .extracting(Book::getTitle)
                    .containsExactly("Spring in Action");
        }

        /**
         * Test: Should filter books by keyword, category, and price range.
         */
        @Test
        @DisplayName("Should filter books by service")
        void filterBooks() {
            createBook("Spring in Action", "Craig Walls", "Programming", 40, 5);
            createBook("Spring Boot Up & Running", "Mark Heckler", "Programming", 45, 4);
            createBook("Cooking 101", "Chef John", "Cooking", 20, 2);

            List<Book> results = bookService.filterBooks(
                    "spring",
                    "Programming",
                    BigDecimal.valueOf(35),
                    BigDecimal.valueOf(50)
            );

            assertThat(results)
                    .extracting(Book::getTitle)
                    .containsExactlyInAnyOrder(
                            "Spring in Action",
                            "Spring Boot Up & Running"
                    );
        }
    }

    @Nested
    @DisplayName("Category retrieval")
    class CategoryTests {

        /**
         * Test: Should return all distinct categories through service.
         */
        @Test
        @DisplayName("Should return distinct categories")
        void findAllCategories() {
            createBook("Spring in Action", "Craig Walls", "Programming", 40, 5);
            createBook("Cooking 101", "Chef John", "Cooking", 20, 2);

            List<String> categories = bookService.findAllCategories();

            assertThat(categories)
                    .containsExactlyInAnyOrder("Programming", "Cooking");
        }
    }
}