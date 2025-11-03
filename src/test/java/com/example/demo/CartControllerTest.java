package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.bind.support.SessionStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock BookRepository bookRepository;
    @Mock CartService cartService;
    @Mock Model model;
    @Mock SessionStatus sessionStatus;

    @InjectMocks CartController controller;

    private Map<Long, Integer> cart;

    @BeforeEach
    void setUp() {
        cart = new HashMap<>();
    }

    // ----------- cart session init -----------
    @Test
    @DisplayName("@ModelAttribute cart: should return a new HashMap when session is empty")
    void cartSession_returnsEmptyMap() {
        Map<Long, Integer> result = controller.cart();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ----------- listBooks() -----------
    @Test
    @DisplayName("listBooks: adds books to model and returns 'shop'")
    void listBooks_addsBooksAndReturnsShop() {
        when(bookRepository.findAll()).thenReturn(java.util.List.of(new Book(), new Book()));

        String view = controller.listBooks(model);

        verify(model).addAttribute(eq("books"), any());
        assertEquals("shop", view);
    }

    // ----------- add() -----------
    @Test
    @DisplayName("add(): calls CartService.add() and redirects to /shop")
    void add_callsServiceAndRedirects() {
        when(cartService.add(anyMap(), eq(1L))).thenReturn(cart);

        String view = controller.add(1L, cart);

        verify(cartService).add(cart, 1L);
        assertEquals("redirect:/shop", view);
    }

    // ----------- remove() -----------
    @Test
    @DisplayName("remove(): calls CartService.remove() and redirects to /cart")
    void remove_callsServiceAndRedirects() {
        when(cartService.remove(anyMap(), eq(2L))).thenReturn(cart);

        String view = controller.remove(2L, cart);

        verify(cartService).remove(cart, 2L);
        assertEquals("redirect:/cart", view);
    }

    // ----------- view cart -----------
    @Test
    @DisplayName("cart(): adds 'items' and 'total' to model and returns 'cart' view")
    void cart_addsItemsAndTotal() {
        when(cartService.detailed(anyMap())).thenReturn(Map.of(new Book(), 1));
        when(cartService.total(anyMap())).thenReturn(100.0);

        String view = controller.cart(cart, model);

        verify(model).addAttribute(eq("items"), any());
        verify(model).addAttribute(eq("total"), eq(100.0));
        assertEquals("cart", view);
    }

    // ----------- clear() -----------
    @Test
    @DisplayName("clear(): sets session complete and redirects to /shop")
    void clear_setsSessionComplete() {
        String view = controller.clear(sessionStatus);

        verify(sessionStatus).setComplete();
        assertEquals("redirect:/shop", view);
    }

    // ----------- pay() -----------
    @Test
    @DisplayName("pay(): calls processPayment(), clears session, redirects with ?paid=true")
    void pay_processPaymentAndClearsSession() {
        String view = controller.pay(cart, sessionStatus);

        verify(cartService).processPayment(cart);
        verify(sessionStatus).setComplete();
        assertEquals("redirect:/shop?paid=true", view);
    }
}
