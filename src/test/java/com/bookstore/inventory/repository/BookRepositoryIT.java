package com.bookstore.inventory.repository;

import com.bookstore.inventory.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link BookRepository}.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Type:</strong> Integration Test (IT)</li>
 *   <li><strong>Layer:</strong> Persistence / JPA repository</li>
 *   <li><strong>Scope:</strong> Custom queries on {@link Book} (search, filter, distinct categories)</li>
 *   <li><strong>Dependencies:</strong> Spring Data JPA, H2 in-memory database, JPA mappings</li>
 *   <li><strong>Framework:</strong> JUnit 5, AssertJ, @DataJpaTest</li>
 * </ul>
 *
 * <h2>Objectives</h2>
 * <ul>
 *   <li>Verify JPQL query correctness against a real database.</li>
 *   <li>Validate search and filter behaviour for keyword/category/price.</li>
 *   <li>Ensure distinct category aggregation works as intended.</li>
 * </ul>
 *
 * <h2>Execution</h2>
 * <pre>
 * mvn test -Dtest=BookRepositoryIT
 * </pre>
 *
 *  * @author Lavji, Fareen
 *  * @version 3.0
 *  * @since 2025-12-01
 */
@DataJpaTest
@DisplayName("BookRepository JPA Integration Tests")
class BookRepositoryIT {

    @Autowired
    private BookRepository bookRepository;

    /**
     * Utility helper to construct and persist a {@link Book} for test setup.
     *
     * @param title    book title
     * @param author   book author
     * @param category category name
     * @param price    price as double
     */
    private void createBook(String title, String author, String category, double price) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setCategory(category);
        b.setPrice(BigDecimal.valueOf(price));
        b.setStock(10);
        bookRepository.save(b);
    }

    /**
     * Tests for the {@link BookRepository#searchBooks(String)} query method.
     */
    @Nested
    @DisplayName("searchBooks() tests")
    class SearchBooksTests {

        /**
         * Test: Should find books by keyword in title, author, or category.
         * Verifies LIKE-based search across multiple fields.
         */
        @Test
        @DisplayName("Should find books by keyword in title, author, or category")
        void searchBooks_byKeyword() {
            createBook("Spring in Action", "Craig Walls", "Programming", 40);
            createBook("Java Concurrency", "Brian Goetz", "Programming", 50);
            createBook("Cooking 101", "Chef John", "Cooking", 20);

            List<Book> results = bookRepository.searchBooks("spring");

            assertThat(results)
                    .extracting(Book::getTitle)
                    .containsExactly("Spring in Action");
        }

        /**
         * Test: Should return an empty list when no book matches the keyword.
         */
        @Test
        @DisplayName("Should return empty list when no match")
        void searchBooks_noMatch() {
            createBook("Spring in Action", "Craig Walls", "Programming", 40);

            List<Book> results = bookRepository.searchBooks("python");

            assertThat(results).isEmpty();
        }
    }

    /**
     * Tests for the {@link BookRepository#filterBooks(String, String, BigDecimal, BigDecimal)} method.
     */
    @Nested
    @DisplayName("filterBooks() tests")
    class FilterBooksTests {

        /**
         * Test: Should filter by keyword, category, and price range.
         * Validates combined filtering logic in JPQL query.
         */
        @Test
        @DisplayName("Should filter by keyword, category, and price range")
        void filterBooks_fullFilter() {
            createBook("Spring in Action", "Craig Walls", "Programming", 40);
            createBook("Spring Boot Up & Running", "Mark Heckler", "Programming", 45);
            createBook("Cooking 101", "Chef John", "Cooking", 20);

            List<Book> results = bookRepository.filterBooks(
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

        /**
         * Test: Should ignore null and empty filters and return all books.
         */
        @Test
        @DisplayName("Should ignore null/empty filters")
        void filterBooks_nullAndEmptyFilters() {
            createBook("Spring in Action", "Craig Walls", "Programming", 40);
            createBook("Cooking 101", "Chef John", "Cooking", 20);

            List<Book> results = bookRepository.filterBooks(
                    null,
                    "",
                    null,
                    null
            );

            assertThat(results).hasSize(2);
        }

        /**
         * Test: Should filter using price range only.
         * Ensures minPrice and maxPrice constraints work without a keyword / category.
         */
        @Test
        @DisplayName("Should filter by price range only")
        void filterBooks_byPriceRangeOnly() {
            createBook("Cheap Book", "Author A", "Misc", 5);
            createBook("Mid Book", "Author B", "Misc", 15);
            createBook("Expensive Book", "Author C", "Misc", 50);

            List<Book> results = bookRepository.filterBooks(
                    "",
                    "",
                    BigDecimal.valueOf(10),
                    BigDecimal.valueOf(20)
            );

            assertThat(results)
                    .extracting(Book::getTitle)
                    .containsExactly("Mid Book");
        }
    }

    /**
     * Tests for the {@link BookRepository#findDistinctCategories()} projection method.
     */
    @Nested
    @DisplayName("findDistinctCategories() tests")
    class DistinctCategoriesTests {

        /**
         * Test: Should return distinct categories present in the database.
         */
        @Test
        @DisplayName("Should return distinct categories")
        void findDistinctCategories() {
            createBook("Spring in Action", "Craig Walls", "Programming", 40);
            createBook("Java Concurrency", "Brian Goetz", "Programming", 50);
            createBook("Cooking 101", "Chef John", "Cooking", 20);

            List<String> categories = bookRepository.findDistinctCategories();

            assertThat(categories)
                    .containsExactlyInAnyOrder("Programming", "Cooking");
        }

        /**
         * Test: Should return an empty list when there are no books.
         */
        @Test
        @DisplayName("Should return empty list when no books")
        void findDistinctCategories_empty() {
            List<String> categories = bookRepository.findDistinctCategories();

            assertThat(categories).isEmpty();
        }
    }
}