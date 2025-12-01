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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShopControllerTest {

    @Test
    void filterByCategory_shouldReturnOnlyBooksInThatCategory() {
        // Mock dependencies
        BookRepository bookRepository = mock(BookRepository.class);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderLineRepository orderLineRepository = mock(OrderLineRepository.class);

        ShopController controller =
                new ShopController(bookRepository, customerRepository, orderRepository, orderLineRepository);

        // Test data
        Book fantasyBook = new Book("The Hobbit", "J.R.R. Tolkien", new BigDecimal("14.99"), "Fantasy", 10);
        Book horrorBook = new Book("It", "Stephen King", new BigDecimal("19.99"), "Horror", 5);

        when(bookRepository.findAll()).thenReturn(List.of(fantasyBook, horrorBook));
        when(bookRepository.findDistinctCategories()).thenReturn(List.of("Fantasy", "Horror"));

        Model model = new ExtendedModelMap();

        String viewName = controller.showShopPage(
                null, "Fantasy",
                null, null,
                null,
                0,                   // page
                model,
                null
        );

        assertEquals("shop", viewName);

        @SuppressWarnings("unchecked")
        List<Book> resultBooks = (List<Book>) model.getAttribute("books");

        assertNotNull(resultBooks);
        assertEquals(1, resultBooks.size());
        assertEquals("Fantasy", resultBooks.get(0).getCategory());
    }

    @Test
    void filterByPriceRange_shouldReturnOnlyBooksWithinRange() {
        BookRepository bookRepository = mock(BookRepository.class);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderLineRepository orderLineRepository = mock(OrderLineRepository.class);

        ShopController controller =
                new ShopController(bookRepository, customerRepository, orderRepository, orderLineRepository);

        Book cheap = new Book("Cheap Book", "Author A", new BigDecimal("5.00"), "Sci-Fi", 10);
        Book expensive = new Book("Expensive Book", "Author B", new BigDecimal("50.00"), "Sci-Fi", 10);

        when(bookRepository.findAll()).thenReturn(List.of(cheap, expensive));
        when(bookRepository.findDistinctCategories()).thenReturn(List.of("Sci-Fi"));

        Model model = new ExtendedModelMap();

        String viewName = controller.showShopPage(
                null, null,
                new BigDecimal("0.00"),
                new BigDecimal("20.00"),
                null,
                0,
                model,
                null
        );

        assertEquals("shop", viewName);

        @SuppressWarnings("unchecked")
        List<Book> resultBooks = (List<Book>) model.getAttribute("books");

        assertNotNull(resultBooks);
        assertEquals(1, resultBooks.size());
        assertTrue(resultBooks.get(0).getPrice().compareTo(new BigDecimal("20.00")) <= 0);
    }

    @Test
    void recommendations_shouldReturnBooksForLoggedInCustomer() {
        // Mocks
        BookRepository bookRepository = mock(BookRepository.class);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderLineRepository orderLineRepository = mock(OrderLineRepository.class);

        ShopController controller =
                new ShopController(bookRepository, customerRepository, orderRepository, orderLineRepository);

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

        // Orders
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

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));
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
                        if (Objects.equals(id, book1.getId())) {
                            result.add(book1);
                        }
                        if (Objects.equals(id, book2.getId())) {
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
                0,
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
