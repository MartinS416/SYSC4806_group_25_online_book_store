package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private CartService cartService;
    private BookRepository bookRepository;
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private OrderService orderService;

    private Customer customer;
    private Cart cart;
    private Book book1, book2;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        cartRepository = mock(CartRepository.class);
        cartItemRepository = mock(CartItemRepository.class);
        orderService = mock(OrderService.class);

        cartService = new CartService(cartRepository, cartItemRepository, bookRepository, orderService);

        customer = new Customer("user","pass","email","John","Doe","12345");
        cart = new Cart(customer);
        ReflectionTestUtils.setField(cart, "id", 1L);

        book1 = new Book("Book A","Author A",10.0,"Fiction",5);
        book2 = new Book("Book B","Author B",15.0,"Non-Fiction",2);
        book1.setId(1L);
        book2.setId(2L);

        CartItem item1 = new CartItem(cart, book1, 2);
        CartItem item2 = new CartItem(cart, book2, 1);
        cart.addItem(item1);
        cart.addItem(item2);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
    }

    /**
     * Add Item to Cart.
     */
    @Test
    void testAddItem() {
        cartService.addItem(cart, book1.getId(), 1);
        assertEquals(3, cart.getItems().stream().filter(i -> i.getBook().equals(book1)).findFirst().get().getQuantity());
        verify(cartItemRepository, atLeastOnce()).save(any(CartItem.class));
    }

    /**
     * Remove Item from Cart.
     */
    @Test
    void testRemoveItem() {
        cartService.removeItem(cart, book1.getId(), 1);
        assertEquals(1, cart.getItems().stream().filter(i -> i.getBook().equals(book1)).findFirst().get().getQuantity());

        cartService.removeItem(cart, book1.getId(), 1);
        assertTrue(cart.getItems().stream().noneMatch(i -> i.getBook().equals(book1)));

        verify(cartItemRepository, atLeastOnce()).delete(any(CartItem.class));
    }

    /**
     * Total cost of the cart.
     */
    @Test
    void testTotal() {
        double total = cartService.total(cart);
        assertEquals(35.0, total);
    }

    /**
     * Checkout creates order lines, reduces stock, and clears cart.
     */
    @Test
    void testCheckout() {
        Order savedOrder = new Order();
        savedOrder.setId(1L);

        when(orderService.create(any(Order.class))).thenReturn(savedOrder);
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        Order order = cartService.checkout(cart.getId());

        assertEquals(3, book1.getStock());
        assertEquals(1, book2.getStock());
        assertEquals(2, order.getOrderLines().size());
        assertTrue(order.getOrderLines().stream().anyMatch(ol -> ol.getBook().equals(book1) && ol.getQuantity() == 2));
        assertTrue(order.getOrderLines().stream().anyMatch(ol -> ol.getBook().equals(book2) && ol.getQuantity() == 1));

        assertTrue(cart.getItems().isEmpty());
        assertFalse(cart.isActive());

        verify(bookRepository, atLeastOnce()).save(book1);
        verify(bookRepository, atLeastOnce()).save(book2);
        verify(cartRepository, atLeastOnce()).save(cart);
        verify(orderService, atLeastOnce()).create(any(Order.class));
    }

    /**
     * Add a new book to the cart.
     */
    @Test
    void testAddNewCartItem() {
        Book book3 = new Book("Book C","Author C",20.0,"Sci-Fi",10);
        book3.setId(3L);
        when(bookRepository.findById(3L)).thenReturn(Optional.of(book3));

        cartService.addItem(cart, book3.getId(), 1);
        assertEquals(3, cart.getItems().size());
        assertTrue(cart.getItems().stream().anyMatch(i -> i.getBook().equals(book3)));
        verify(cartItemRepository, atLeastOnce()).save(any(CartItem.class));
    }

    /**
     * Remove item completely if quantity exceeds.
     */
    @Test
    void testRemoveItemCompletely() {
        cartService.removeItem(cart, book2.getId(), 5);
        assertFalse(cart.getItems().stream().anyMatch(i -> i.getBook().equals(book2)));
        verify(cartItemRepository, atLeastOnce()).delete(any(CartItem.class));
    }

    /**
     * Detailed mapping: Book -> CartItem.
     */
    @Test
    void testDetailedMapping() {
        var detailed = cartService.detailed(cart);
        assertEquals(2, detailed.size());
        assertTrue(detailed.containsKey(book1));
        assertTrue(detailed.containsKey(book2));
    }

    // EDGE CASES

    @Test
    void testAddNegativeQuantity() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cartService.addItem(cart, book1.getId(), -2));
        assertEquals("Quantity must be positive", ex.getMessage());
    }

    @Test
    void testRemoveNegativeQuantity() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cartService.removeItem(cart, book1.getId(), -1));
        assertEquals("Quantity must be positive", ex.getMessage());
    }

    @Test
    void testRemoveNonExistingBook() {
        Book book3 = new Book("Book C","Author C",20.0,"Sci-Fi",10);
        book3.setId(3L);
        assertDoesNotThrow(() -> cartService.removeItem(cart, book3.getId(), 1));
    }

    @Test
    void testCheckoutEmptyCart() {
        Cart emptyCart = new Cart(customer);
        ReflectionTestUtils.setField(emptyCart, "id", 2L);
        when(cartRepository.findById(emptyCart.getId())).thenReturn(Optional.of(emptyCart));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> cartService.checkout(emptyCart.getId()));
        assertEquals("Cannot checkout an empty cart", ex.getMessage());
    }

    @Test
    void testTotalEmptyCart() {
        Cart emptyCart = new Cart(customer);
        double total = cartService.total(emptyCart);
        assertEquals(0.0, total);
    }

    @Test
    void testDetailedMappingEmptyCart() {
        Cart emptyCart = new Cart(customer);
        var detailed = cartService.detailed(emptyCart);
        assertTrue(detailed.isEmpty());
    }
}