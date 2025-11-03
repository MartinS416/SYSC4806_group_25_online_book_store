package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Book entity class.
 * Covers constructors, getters/setters, and equality-like behavior.
 */
class BookTest {

    @Test
    @DisplayName("Default constructor initializes fields to default values")
    void defaultConstructor_initializesDefaults() {
        Book book = new Book();
        assertNull(book.getId());
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertEquals(0.0, book.getPrice());
        assertNull(book.getCategory());
        assertEquals(0, book.getStock());
    }

    @Test
    @DisplayName("Parameterized constructor assigns all provided values correctly")
    void parameterizedConstructor_assignsValues() {
        Book b = new Book("Clean Code", "Robert C. Martin", 45.99, "Programming", 10);

        assertEquals("Clean Code", b.getTitle());
        assertEquals("Robert C. Martin", b.getAuthor());
        assertEquals(45.99, b.getPrice());
        assertEquals("Programming", b.getCategory());
        assertEquals(10, b.getStock());
    }

    @Test
    @DisplayName("Setters correctly update values and getters return them")
    void settersAndGetters_workCorrectly() {
        Book b = new Book();

        b.setId(1L);
        b.setTitle("Design Patterns");
        b.setAuthor("GoF");
        b.setPrice(55.50);
        b.setCategory("Software Engineering");
        b.setStock(7);

        assertEquals(1L, b.getId());
        assertEquals("Design Patterns", b.getTitle());
        assertEquals("GoF", b.getAuthor());
        assertEquals(55.50, b.getPrice());
        assertEquals("Software Engineering", b.getCategory());
        assertEquals(7, b.getStock());
    }

    @Test
    @DisplayName("Books with identical field values behave equivalently")
    void booksWithSameValues_matchByFields() {
        Book b1 = new Book("Clean Architecture", "Robert C. Martin", 39.99, "Programming", 5);
        b1.setId(100L);

        Book b2 = new Book("Clean Architecture", "Robert C. Martin", 39.99, "Programming", 5);
        b2.setId(100L);

        // Since equals() isn't overridden, compare field-by-field
        assertEquals(b1.getId(), b2.getId());
        assertEquals(b1.getTitle(), b2.getTitle());
        assertEquals(b1.getAuthor(), b2.getAuthor());
        assertEquals(b1.getPrice(), b2.getPrice());
        assertEquals(b1.getCategory(), b2.getCategory());
        assertEquals(b1.getStock(), b2.getStock());
    }

    @Test
    @DisplayName("Modifying one book’s values does not affect another book instance")
    void modifyingOneBook_doesNotAffectAnother() {
        Book b1 = new Book("Refactoring", "Martin Fowler", 49.99, "Software", 3);
        Book b2 = new Book("Refactoring", "Martin Fowler", 49.99, "Software", 3);

        b1.setPrice(59.99);
        b1.setStock(1);

        assertNotEquals(b1.getPrice(), b2.getPrice());
        assertNotEquals(b1.getStock(), b2.getStock());
    }

    @Test
    @DisplayName("Price and stock handle edge cases (zero or negative values)")
    void priceAndStock_edgeCases() {
        Book b = new Book();
        b.setPrice(0.0);
        b.setStock(-5);

        assertEquals(0.0, b.getPrice());
        assertEquals(-5, b.getStock());
    }

    @Test
    @DisplayName("toString() should include key book fields for debugging")
    void toString_includesImportantFields() {
        Book b = new Book("Domain-Driven Design", "Eric Evans", 60.00, "Software", 2);
        b.setId(200L);

        String text = b.toString();
        assertTrue(text.contains("Domain-Driven Design"));
        assertTrue(text.contains("Eric Evans"));
    }
}
