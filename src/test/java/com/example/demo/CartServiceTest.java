package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    CartService cartService;

    private Map<Long,Integer> cart;

    private static Book book(long id, String title, double price, int stock) {
        Book b = new Book();
        b.setId(id);
        b.setTitle(title);
        b.setPrice(price);   // double in your model
        b.setStock(stock);
        return b;
    }

    @BeforeEach
    void setUp() {
        cart = new HashMap<>();
    }

    // ---------------------- add/remove ----------------------

    @Test
    @DisplayName("add: inserts new item with qty=1 and validates book exists")
    void add_insertsNewItem() {
        Book cleanCode = book(1L, "Clean Code", 45.0, 10);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(cleanCode));

        Map<Long,Integer> result = cartService.add(cart, 1L);

        assertEquals(1, result.size());
        assertEquals(1, result.get(1L));
        // ensure same instance (method mutates and returns the same map)
        assertSame(cart, result);
    }

    @Test
    @DisplayName("add: increments existing quantity")
    void add_incrementsExisting() {
        cart.put(2L, 2);
        Book ddd = book(2L, "DDD", 60.0, 5);
        when(bookRepository.findById(2L)).thenReturn(Optional.of(ddd));

        cartService.add(cart, 2L);

        assertEquals(3, cart.get(2L));
    }

    @Test
    @DisplayName("remove: decrements quantity; removes entry when it reaches zero")
    void remove_decrementsAndRemoves() {
        cart.put(3L, 2);
        when(bookRepository.findById(3L)).thenReturn(Optional.of(book(3L, "Refactoring", 50.0, 7)));

        // first remove -> qty 1
        cartService.remove(cart, 3L);
        assertEquals(1, cart.get(3L));

        // second remove -> gone
        cartService.remove(cart, 3L);
        assertFalse(cart.containsKey(3L));
    }

    @Test
    @DisplayName("add: throws when bookId not found (orElseThrow)")
    void add_throwsWhenMissingBook() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> cartService.add(cart, 999L));
    }

    // ---------------------- total ----------------------

    @Test
    @DisplayName("total: sums price × qty using repository lookups")
    void total_sums() {
        cart.put(10L, 1);  // 30.0
        cart.put(11L, 2);  // 55.5 x 2

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book(10L, "SICP", 30.0, 100)));
        when(bookRepository.findById(11L)).thenReturn(Optional.of(book(11L, "PoEAA", 55.5, 100)));

        double total = cartService.total(cart);

        assertEquals(141.0, total, 1e-9); // 30 + 111 = 141
    }

    // ---------------------- detailed ----------------------

    @Test
    @DisplayName("detailed: returns map<Book,qty> for existing ids")
    void detailed_mapsBooks() {
        cart.put(20L, 3);
        cart.put(21L, 1);

        Book b20 = book(20L, "Clean Architecture", 40.0, 5);
        Book b21 = book(21L, "TDD", 35.0, 2);
        when(bookRepository.findById(20L)).thenReturn(Optional.of(b20));
        when(bookRepository.findById(21L)).thenReturn(Optional.of(b21));

        Map<Book,Integer> detailed = cartService.detailed(cart);

        assertEquals(2, detailed.size());
        assertEquals(3, detailed.get(b20));
        assertEquals(1, detailed.get(b21));
    }

    // ---------------------- processPayment ----------------------

    @Test
    @DisplayName("processPayment: reduces stock by qty (not below 0) and saves each book")
    void processPayment_updatesStockAndSaves() {
        cart.put(30L, 2);
        cart.put(31L, 10);

        Book b30 = book(30L, "Algorithms", 70.0, 5);   // 5 -> 3
        Book b31 = book(31L, "Tiny Stock", 10.0, 6);    // 6 -> 0 (not negative)

        when(bookRepository.findById(30L)).thenReturn(Optional.of(b30));
        when(bookRepository.findById(31L)).thenReturn(Optional.of(b31));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        cartService.processPayment(cart);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(2)).save(captor.capture());

        // order of saves not guaranteed; check both
        boolean saw30 = false, saw31 = false;
        for (Book b : captor.getAllValues()) {
            if (b.getId() == 30L) { assertEquals(3, b.getStock()); saw30 = true; }
            if (b.getId() == 31L) { assertEquals(0, b.getStock()); saw31 = true; }
        }
        assertTrue(saw30 && saw31, "Both books should have been saved with updated stock");
    }

    // ---------------------- edge: total/detailed missing id ----------------------

    @Nested
    class MissingIdEdges {
        @Test
        @DisplayName("total: missing id triggers orElseThrow")
        void total_missingId_throws() {
            cart.put(777L, 1);
            when(bookRepository.findById(777L)).thenReturn(Optional.empty());
            assertThrows(Exception.class, () -> cartService.total(cart));
        }

        @Test
        @DisplayName("detailed: missing id triggers orElseThrow")
        void detailed_missingId_throws() {
            cart.put(888L, 2);
            when(bookRepository.findById(888L)).thenReturn(Optional.empty());
            assertThrows(Exception.class, () -> cartService.detailed(cart));
        }
    }
}
