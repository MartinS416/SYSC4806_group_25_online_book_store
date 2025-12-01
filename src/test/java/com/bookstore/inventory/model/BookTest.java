package com.bookstore.inventory.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Book} entity.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Category:</strong> Unit Tests (UT)</li>
 *   <li><strong>Scope:</strong> Book model validation, getters/setters, constructor behavior</li>
 *   <li><strong>Dependencies:</strong> None (pure POJO testing)</li>
 *   <li><strong>Test Levels:</strong> Basic functionality, boundary conditions, state transitions</li>
 *   <li><strong>Framework:</strong> JUnit 5 + AssertJ</li>
 *   <li><strong>Test Count:</strong> 14 test cases organized in 5 nested groups</li>
 * </ul>
 *
 * <h2>Test Coverage</h2>
 * <ul>
 *   <li>Constructor tests: default and parameterized initialization</li>
 *   <li>Property tests: all fields (id, title, author, price, shop)</li>
 *   <li>Validation tests: null, empty string, and boundary value handling</li>
 *   <li>State management: instance isolation and independent updates</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>
 * mvn test -Dtest=BookTest
 * </pre>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DisplayName("Book Entity Tests")
class BookTest {

    private Book book;

    /**
     * Initializes a fresh Book instance before each test.
     * Ensures test isolation and prevents state leakage between test cases.
     */
    @BeforeEach
    void setUp() {
        book = new Book();
    }

    /**
     * Tests for Book constructor behaviour.
     * Validates both default and parameterized constructor initialization.
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        /**
         * Test: Should create Book with the default constructor.
         * Validates that a default-constructed Book has null/default values.
         */
        @Test
        @DisplayName("Should create Book with default constructor")
        void testDefaultConstructor() {
            // Arrange & Act
            Book newBook = new Book();

            // Assert
            assertThat(newBook).isNotNull();
            assertThat(newBook.getId()).isNull();
            assertThat(newBook.getTitle()).isNull();
            assertThat(newBook.getAuthor()).isNull();
            assertThat(newBook.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        /**
         * Test: Should create Book with parameterized constructor.
         * Validates that all parameters are correctly assigned during construction.
         */
        @Test
        @DisplayName("Should create Book with parameterized constructor")
        void testParameterizedConstructor() {
            // Arrange
            String title = "Test Book";
            String author = "Test Author";
            BigDecimal price = BigDecimal.valueOf(29.99);
            String category = "Test Category";
            int stock = 3;

            // Act
            Book newBook = new Book(title, author, price, category, stock);

            // Assert
            assertThat(newBook.getTitle()).isEqualTo(title);
            assertThat(newBook.getAuthor()).isEqualTo(author);
            assertThat(newBook.getPrice()).isEqualTo(price);
            assertThat(newBook.getCategory()).isEqualTo(category);
            assertThat(newBook.getStock()).isEqualTo(stock);
        }
    }

    /**
     * Tests for Book getter and setter methods.
     * Validates that each property can be independently read and written.
     */
    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        /**
         * Test: Should set and get title.
         * Validates title property assignment and retrieval.
         */
        @Test
        @DisplayName("Should set and get title")
        void testTitleGetterSetter() {
            // Arrange
            String title = "Spring in Action";

            // Act
            book.setTitle(title);

            // Assert
            assertThat(book.getTitle()).isEqualTo(title);
        }

        /**
         * Test: Should set and get author.
         * Validates author property assignment and retrieval.
         */
        @Test
        @DisplayName("Should set and get author")
        void testAuthorGetterSetter() {
            // Arrange
            String author = "Craig Walls";

            // Act
            book.setAuthor(author);

            // Assert
            assertThat(book.getAuthor()).isEqualTo(author);
        }

        /**
         * Test: Should set and get price.
         * Validates price property assignment and retrieval with numeric values.
         */
        @Test
        @DisplayName("Should set and get price")
        void testPriceGetterSetter() {
            // Arrange
            BigDecimal price = BigDecimal.valueOf(49.99);

            // Act
            book.setPrice(price);

            // Assert
            assertThat(book.getPrice()).isEqualTo(price);
        }

        /**
         * Test: Should set and get id.
         * Validates id property assignment and retrieval for unique identification.
         */
        @Test
        @DisplayName("Should set and get id")
        void testIdGetterSetter() {
            // Arrange
            Long id = 1L;

            // Act
            book.setId(id);

            // Assert
            assertThat(book.getId()).isEqualTo(id);
        }

        /**
         * Test: Should set and get category.
         * Validates categories property assignment and retrieval for unique identification.
         */
        @Test
        @DisplayName("Should set and get categories")
        void testCategoryGetterSetter() {
            // Arrange
            String category = "Technology";

            // Act
            book.setCategory(category);

            // Assert
            assertThat(book.getCategory()).isEqualTo(category);
        }

        /**
         * Test: Should set and get stock.
         * Validates the relationship with stock field and inventory management.
         */
        @Test
        @DisplayName("Should set and get stock")
        void testShopGetterSetter() {
            // Arrange
            int stock = 10;

            // Act
            book.setStock(stock);

            // Assert
            assertThat(book.getStock()).isEqualTo(stock);
        }
    }

    /**
     * Tests for Book field validation and boundary conditions.
     * Validates handling of null, empty, and edge-case values.
     */
    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        /**
         * Test: Should allow null title.
         * Validates that title fields accept a null value without exception.
         */
        @Test
        @DisplayName("Should allow null title")
        void testNullTitle() {
            // Act
            book.setTitle(null);

            // Assert
            assertThat(book.getTitle()).isNull();
        }

        /**
         * Test: Should allow null author.
         * Validates that the author field accepts null values without exception.
         */
        @Test
        @DisplayName("Should allow null author")
        void testNullAuthor() {
            // Act
            book.setAuthor(null);

            // Assert
            assertThat(book.getAuthor()).isNull();
        }

        /**
         * Test: Should allow zero prices.
         * Validates that the price field accepts zero value (boundary case).
         */
        @Test
        @DisplayName("Should allow zero price")
        void testZeroPrice() {
            // Act
            book.setPrice(BigDecimal.valueOf(0.0));

            // Assert
            assertThat(book.getPrice()).isZero();
        }

        /**
         * Test: Should allow negative price.
         * Validates that the price field accepts negative values (edge case).
         */
        @Test
        @DisplayName("Should allow negative price")
        void testNegativePrice() {
            // Act
            book.setPrice(BigDecimal.valueOf(-10.0));

            // Assert
            assertThat(book.getPrice()).isEqualTo(BigDecimal.valueOf(-10.0));
        }

        /**
         * Test: Should handle empty string title.
         * Validates that title fields accept empty strings without exception.
         */
        @Test
        @DisplayName("Should handle empty string title")
        void testEmptyStringTitle() {
            // Act
            book.setTitle("");

            // Assert
            assertThat(book.getTitle()).isEmpty();
        }
    }

    /**
     * Tests for Book state management and instance isolation.
     * Validates that instances maintain an independent state and support multiple updates.
     */
    @Nested
    @DisplayName("State Management Tests")
    class StateManagementTests {

        /**
         * Test: Should maintain independent state across instances.
         * Validates that multiple Book instances do not interfere with each other's state.
         */
        @Test
        @DisplayName("Should maintain independent state across instances")
        void testInstanceIsolation() {
            // Arrange
            Book book1 = new Book("Book 1", "Author 1", BigDecimal.valueOf(10.0), "Category 1", 10);
            Book book2 = new Book("Book 2", "Author 2", BigDecimal.valueOf(20.0), "Category 2", 5);

            // Act & Assert
            assertThat(book1.getTitle()).isNotEqualTo(book2.getTitle());
            assertThat(book1.getAuthor()).isNotEqualTo(book2.getAuthor());
            assertThat(book1.getPrice()).isNotEqualTo(book2.getPrice());
            assertThat(book1.getCategory()).isNotEqualTo(book2.getCategory());
            assertThat(book1.getStock()).isNotEqualTo(book2.getStock());
        }

        /**
         * Test: Should update price multiple times.
         * Validates that properties can be updated multiple times without issues.
         */
        @Test
        @DisplayName("Should update price multiple times")
        void testMultiplePriceUpdates() {
            // Act
            book.setPrice(BigDecimal.valueOf(10.0));
            assertThat(book.getPrice()).isEqualTo(BigDecimal.valueOf(10.0));

            book.setPrice(BigDecimal.valueOf(15.0));
            assertThat(book.getPrice()).isEqualTo(BigDecimal.valueOf(15.0));

            book.setPrice(BigDecimal.valueOf(20.0));

            // Assert
            assertThat(book.getPrice()).isEqualTo(BigDecimal.valueOf(20.0));
        }
    }
}