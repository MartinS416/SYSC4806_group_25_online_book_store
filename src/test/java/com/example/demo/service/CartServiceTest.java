package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CartService (repository-backed).
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLineRepository orderLineRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private Book book1;
    private Book book2;
    private Address address;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        ReflectionTestUtils.setField(customer, "id", 42L);

        cart = new Cart(customer);
        ReflectionTestUtils.setField(cart, "id", 10L);

        book1 = new Book();
        book1.setId(1L);
        book1.setPrice(BigDecimal.valueOf(10.0));

        book2 = new Book();
        book2.setId(2L);
        book2.setPrice(BigDecimal.valueOf(15.0));

        address = new Address();
        address.setCustomer(customer);
        address.setFirstName("john");
        address.setLastName("johnson");
        address.setStreet("123 street RD");
        address.setCity("Ottawa");
        address.setRegion("Ont");
        address.setCountry("CA");
        address.setPostcode("A1A 1A1");
    }

    @Test
    void addItem_persistsNewCartItem() {
        when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartItem saved = cartService.addItem(10L, 1L, 1);

        assertNotNull(saved);
        assertEquals(1, saved.getQuantity());
        assertEquals(book1, saved.getBook());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(cartRepository, atLeastOnce()).save(cart);
    }

    @Test
    void addItem_negativeQuantity_throws() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem(10L, 1L, -1));
        verifyNoInteractions(cartRepository, bookRepository, cartItemRepository);
    }

    @Test
    void removeItem_negativeQuantity_throws() {
        assertThrows(IllegalArgumentException.class, () -> cartService.removeItem(10L, 1L, -5));
        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    @Test
    void checkout_emptyCart_throwsExactMessage() {
        cart.setItems(new ArrayList<>()); // empty
        when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> cartService.checkout(10L,address));
        assertEquals("Cannot checkout an empty cart", ex.getMessage());

        verify(orderRepository, never()).save(any(Order.class));
        verify(orderLineRepository, never()).save(any(OrderLine.class));
    }

    @Test
    void checkout_createsOrderLines_and_clearsCart() {
        // prepare cart items
        CartItem ci1 = new CartItem();
        ci1.setBook(book1);
        ci1.setQuantity(1);

        CartItem ci2 = new CartItem();
        ci2.setBook(book2);
        ci2.setQuantity(1);

        cart.addItem(ci1);
        cart.addItem(ci2);

        when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            ReflectionTestUtils.setField(o, "id", 100L);
            return o;
        });

        when(orderLineRepository.save(any(OrderLine.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // run the method under test
        int created = cartService.checkout(10L,address);

        assertEquals(2, created);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderLineRepository, times(2)).save(any(OrderLine.class));
        // verify cart items removed via repository are deleted, and the cart is saved
        verify(cartItemRepository, times(2)).delete(any(CartItem.class));
        verify(cartRepository, times(1)).save(cart);
        assertTrue(cart.getItems().isEmpty());
    }
}