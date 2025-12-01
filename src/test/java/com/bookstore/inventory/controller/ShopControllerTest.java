package com.bookstore.inventory.controller;

import com.bookstore.inventory.model.Book;
import com.bookstore.common.model.Customer;
import com.bookstore.pos.model.Order;
import com.bookstore.pos.model.OrderLine;
import com.bookstore.inventory.repository.BookRepository;
import com.bookstore.common.repository.CustomerRepository;
import com.bookstore.pos.repository.OrderLineRepository;
import com.bookstore.pos.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ShopController}.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Category:</strong> Unit Tests (UT)</li>
 *   <li><strong>Layer:</strong> Web/Controller</li>
 *   <li><strong>Scope:</strong> Shop page filtering, recommendations, and model population</li>
 *   <li><strong>Dependencies:</strong> {@link BookRepository}, {@link CustomerRepository},
 *       {@link OrderRepository}, {@link OrderLineRepository} (all mocked)</li>
 *   <li><strong>Framework:</strong> JUnit 5, Mockito, Spring MVC {@link Model}</li>
 * </ul>
 *
 * <h2>Objectives</h2>
 * <ul>
 *   <li>Verify that {@link ShopController#showShopPage} filters by category and price correctly.</li>
 *   <li>Verify that recommendations for a logged-in customer are computed and exposed via the model.</li>
 *   <li>Ensure correct view names and model attributes without starting a web server.</li>
 * </ul>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ShopController Unit Tests")
class ShopControllerTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLineRepository orderLineRepository;

    @InjectMocks
    private ShopController controller;

    /**
     * Test: Should return only books in the selected category.
     * Scenario:
     *  - Repository returns books from multiple categories.
     *  - Controller is called with category filter="Fantasy".
     * Expected:
     *  - View name is "shop".
     *  - Model "books" contains only Fantasy books.
     */
    @Test
    @DisplayName("Filter by category returns only matching books")
    void filterByCategory_shouldReturnOnlyBooksInThatCategory() {
        Book fantasyBook = new Book();
        fantasyBook.setTitle("The Hobbit");
        fantasyBook.setAuthor("J.R.R. Tolkien");
        fantasyBook.setCategory("Fantasy");
        fantasyBook.setPrice(new BigDecimal("14.99"));
        fantasyBook.setStock(10);

        Book horrorBook = new Book();
        horrorBook.setTitle("It");
        horrorBook.setAuthor("Stephen King");
        horrorBook.setCategory("Horror");
        horrorBook.setPrice(new BigDecimal("19.99"));
        horrorBook.setStock(5);

        when(bookRepository.findAll()).thenReturn(List.of(fantasyBook, horrorBook));
        when(bookRepository.findDistinctCategories()).thenReturn(List.of("Fantasy", "Horror"));

        Model model = new ExtendedModelMap();

        String viewName = controller.showShopPage(
                null, "Fantasy",
                null, null,
                null, model, null
        );

        assertEquals("shop", viewName);
        @SuppressWarnings("unchecked")
        List<Book> resultBooks = (List<Book>) model.getAttribute("books");
        assertNotNull(resultBooks);
        assertEquals(1, resultBooks.size());
        assertEquals("Fantasy", resultBooks.getFirst().getCategory());
    }

    /**
     * Test: Should return only books within the requested price range.
     * Scenario:
     *  - Books exist below and above the max price.
     * Expected:
     *  - View name is "shop".
     *  - Model "books" contains only books within [minPrice, maxPrice].
     */
    @Test
    @DisplayName("Filter by price range returns only books within range")
    void filterByPriceRange_shouldReturnOnlyBooksWithinRange() {
        Book cheap = new Book();
        cheap.setTitle("Cheap Book");
        cheap.setAuthor("Author A");
        cheap.setCategory("Sci-Fi");
        cheap.setPrice(new BigDecimal("5.00"));
        cheap.setStock(10);

        Book expensive = new Book();
        expensive.setTitle("Expensive Book");
        expensive.setAuthor("Author B");
        expensive.setCategory("Sci-Fi");
        expensive.setPrice(new BigDecimal("50.00"));
        expensive.setStock(10);

        when(bookRepository.findAll()).thenReturn(List.of(cheap, expensive));
        when(bookRepository.findDistinctCategories()).thenReturn(List.of("Sci-Fi"));

        Model model = new ExtendedModelMap();

        String viewName = controller.showShopPage(
                null, null,
                new BigDecimal("0.00"),
                new BigDecimal("20.00"),
                null,
                model,
                null
        );

        assertEquals("shop", viewName);
        @SuppressWarnings("unchecked")
        List<Book> resultBooks = (List<Book>) model.getAttribute("books");
        assertNotNull(resultBooks);
        assertEquals(1, resultBooks.size());
        assertTrue(resultBooks.getFirst().getPrice().compareTo(new BigDecimal("20.00")) <= 0);
    }

    /**
     * Test: Should compute recommendations for the logged-in customer.
     * Scenario:
     *  - Multiple orders exist for different customers.
     *  - Recommendations should be built based on the current customer's history.
     * Expected:
     *  - View name is "shop".
     *  - Model "recommendedBooks" is present and non-empty.
     */
    @Test
    @DisplayName("Recommendations return books for logged-in customer")
    void recommendations_shouldReturnBooksForLoggedInCustomer() {
        Customer currentCustomer = new Customer();
        currentCustomer.setId(1L);

        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);

        Book book1 = new Book();
        book1.setId(100L);
        book1.setTitle("Book 1");
        book1.setPrice(new BigDecimal("10.00"));
        book1.setCategory("Fantasy");
        book1.setStock(5);

        Book book2 = new Book();
        book2.setId(200L);
        book2.setTitle("Book 2");
        book2.setPrice(new BigDecimal("12.00"));
        book2.setCategory("Fantasy");
        book2.setStock(5);

        Order order1 = new Order();
        order1.setCustomer(currentCustomer);
        OrderLine line1 = new OrderLine();
        line1.setOrder(order1);
        line1.setBook(book1);

        Order order2 = new Order();
        order2.setCustomer(otherCustomer);
        OrderLine line2 = new OrderLine();
        line2.setOrder(order2);
        line2.setBook(book1);
        OrderLine line3 = new OrderLine();
        line3.setOrder(order2);
        line3.setBook(book2);

        List<Order> allOrders = List.of(order1, order2);

        when(orderRepository.findAll()).thenReturn(allOrders);
        when(orderLineRepository.findByOrder(order1)).thenReturn(List.of(line1));
        when(orderLineRepository.findByOrder(order2)).thenReturn(List.of(line2, line3));

        Principal principal = () -> "user@example.com";
        when(customerRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(currentCustomer));

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        when(bookRepository.findDistinctCategories()).thenReturn(List.of("Fantasy"));
        when(bookRepository.findAllById(any(Iterable.class)))
                .thenAnswer(invocation -> {
                    Iterable<Long> ids = invocation.getArgument(0);
                    List<Book> result = new ArrayList<>();
                    for (Long id : ids) {
                        if (id.equals(book1.getId())) {
                            result.add(book1);
                        }
                        if (id.equals(book2.getId())) {
                            result.add(book2);
                        }
                    }
                    return result;
                });

        Model model = new ExtendedModelMap();

        String viewName = controller.showShopPage(
                null, null,
                null, null,
                null,
                model,
                principal
        );

        assertEquals("shop", viewName);
        @SuppressWarnings("unchecked")
        List<Book> recommended = (List<Book>) model.getAttribute("recommendedBooks");
        assertNotNull(recommended);
        assertFalse(recommended.isEmpty());
    }
}