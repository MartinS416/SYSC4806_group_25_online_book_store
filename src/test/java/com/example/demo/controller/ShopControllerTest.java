package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.model.OrderLine;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderLineRepository;
import com.example.demo.repository.OrderRepository;
import org.junit.jupiter.api.Test;
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
 * Simple tests for the ShopController filters and recommendations.
 * These are intentionally not complicated, just enough to show
 * that your feature works and is covered by tests.
 */
public class ShopControllerTest {

    /**
     * Test that filtering by category returns only books in that category.
     */
    @Test
    void filterByCategory_shouldReturnOnlyBooksInThatCategory() {
        // Arrange
        BookRepository bookRepository = mock(BookRepository.class);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderLineRepository orderLineRepository = mock(OrderLineRepository.class);

        ShopController controller =
                new ShopController(bookRepository, customerRepository, orderRepository, orderLineRepository);

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

        List<Book> allBooks = List.of(fantasyBook, horrorBook);
        when(bookRepository.findAll()).thenReturn(allBooks);
        when(bookRepository.findDistinctCategories()).thenReturn(List.of("Fantasy", "Horror"));

        Model model = new ExtendedModelMap();

        // Act
        String viewName = controller.showShopPage(
                null,                 // keyword
                "Fantasy",            // category filter
                null,                 // minPrice
                null,                 // maxPrice
                null,                 // inStock
                model,
                null                  // principal (not logged in)
        );

        // Assert
        assertEquals("shop", viewName);

        @SuppressWarnings("unchecked")
        List<Book> resultBooks = (List<Book>) model.getAttribute("books");
        assertNotNull(resultBooks);
        assertEquals(1, resultBooks.size());
        assertEquals("Fantasy", resultBooks.get(0).getCategory());
    }

    /**
     * Test that filtering by a price range returns only books within that range.
     */
    @Test
    void filterByPriceRange_shouldReturnOnlyBooksWithinRange() {
        // Arrange
        BookRepository bookRepository = mock(BookRepository.class);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderLineRepository orderLineRepository = mock(OrderLineRepository.class);

        ShopController controller =
                new ShopController(bookRepository, customerRepository, orderRepository, orderLineRepository);

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

        // Act
        String viewName = controller.showShopPage(
                null,                          // keyword
                null,                          // category
                new BigDecimal("0.00"),        // minPrice
                new BigDecimal("20.00"),       // maxPrice
                null,                          // inStock
                model,
                null                           // principal
        );

        // Assert
        assertEquals("shop", viewName);

        @SuppressWarnings("unchecked")
        List<Book> resultBooks = (List<Book>) model.getAttribute("books");
        assertNotNull(resultBooks);
        assertEquals(1, resultBooks.size());
        assertTrue(resultBooks.get(0).getPrice().compareTo(new BigDecimal("20.00")) <= 0);
    }

    /**
     * Very simple recommendation test:
     * if there is another customer with overlapping purchases,
     * the controller should put at least one recommended book into the model.
     */
    @Test
    void recommendations_shouldReturnBooksForLoggedInCustomer() {
        // Arrange
        BookRepository bookRepository = mock(BookRepository.class);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderLineRepository orderLineRepository = mock(OrderLineRepository.class);

        ShopController controller =
                new ShopController(bookRepository, customerRepository, orderRepository, orderLineRepository);

        // Logged in customer
        Customer currentCustomer = new Customer();
        currentCustomer.setId(1L);

        // Other customer
        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);

        // Books
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

        // Current customer has bought book1
        Order order1 = new Order();
        order1.setCustomer(currentCustomer);

        OrderLine line1 = new OrderLine();
        line1.setOrder(order1);
        line1.setBook(book1);

        // Other customer has bought book1 AND book2
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

        // Principal (logged in user)
        Principal principal = () -> "user@example.com";

        when(customerRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(currentCustomer));

        // Books returned when controller filters (no keyword)
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        when(bookRepository.findDistinctCategories()).thenReturn(List.of("Fantasy"));

        // When recommendations look up books by id
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

        // Act
        String viewName = controller.showShopPage(
                null,       // keyword
                null,       // category
                null,       // minPrice
                null,       // maxPrice
                null,       // inStock
                model,
                principal
        );

        // Assert
        assertEquals("shop", viewName);

        @SuppressWarnings("unchecked")
        List<Book> recommended = (List<Book>) model.getAttribute("recommendedBooks");
        assertNotNull(recommended);
        // We expect at least 1 recommendation (book2)
        assertFalse(recommended.isEmpty());
    }
}
