package com.bookstore.pos.service;

import com.bookstore.common.model.Address;
import com.bookstore.inventory.model.Book;
import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.AddressRepository;
import com.bookstore.inventory.repository.BookRepository;
import com.bookstore.common.repository.CustomerRepository;
import com.bookstore.pos.model.Cart;
import com.bookstore.pos.model.CartItem;
import com.bookstore.pos.model.Order;
import com.bookstore.pos.model.OrderLine;
import com.bookstore.pos.repository.CartItemRepository;
import com.bookstore.pos.repository.CartRepository;
import com.bookstore.pos.repository.OrderLineRepository;
import com.bookstore.pos.repository.OrderRepository;
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
 * Unit tests for {@link CartService} in the POS domain.
 * <p>
 * These tests verify cart operations and checkout behavior using Mockito
 * without loading a Spring context or database.
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
    private Customer customer;
    private Book book1;
    private Book book2;
    private Address address;

    @BeforeEach
    void setUp() {
        customer = new Customer();
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

    /**
     * Verifies that {@link CartService#findOrCreateCartForCustomer(Long)} creates
     * a new active cart when none exists for the customer.
     */
    @Test
    void findOrCreateCartForCustomer_createsNewWhenNoneExists() {
        when(cartRepository.findByCustomerIdAndActiveTrue(42L)).thenReturn(Optional.empty());
        when(customerRepository.findById(42L)).thenReturn(Optional.of(customer));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart c = inv.getArgument(0);
            ReflectionTestUtils.setField(c, "id", 99L);
            return c;
        });

        Cart result = cartService.findOrCreateCartForCustomer(42L);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals(customer, result.getCustomer());
        verify(cartRepository).save(any(Cart.class));
    }

    /**
     * Verifies that {@link CartService#addItem(Long, Long, int)} creates a new cart item
     * and persists it when adding a positive quantity.
     */
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
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository, atLeastOnce()).save(cart);
    }

    /**
     * Verifies that adding a negative quantity throws an {@link IllegalArgumentException}
     * and does not interact with repositories.
     */
    @Test
    void addItem_negativeQuantity_throws() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem(10L, 1L, -1));
        verifyNoInteractions(cartRepository, bookRepository, cartItemRepository);
    }

    /**
     * Verifies that removing a negative quantity throws an {@link IllegalArgumentException}
     * and does not interact with the cart repository.
     */
    @Test
    void removeItem_negativeQuantity_throws() {
        assertThrows(IllegalArgumentException.class, () -> cartService.removeItem(10L, 1L, -5));
        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    /**
     * Verifies that attempting to check out an empty cart throws an
     * {@link IllegalStateException} with the expected message and does not
     * create any orders or order lines.
     */
    @Test
    void checkout_emptyCart_throwsExactMessage() {
        cart.setItems(new ArrayList<>()); // empty
        when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> cartService.checkout(10L, address));
        assertEquals("Cannot checkout an empty cart", ex.getMessage());

        verify(orderRepository, never()).save(any(Order.class));
        verify(orderLineRepository, never()).save(any(OrderLine.class));
    }

    /**
     * Verifies that a non-empty cart produces an order and order lines, but
     * that the cart itself is not cleared by {@link CartService#checkout}.
     */
    @Test
    void checkout_createsOrderAndOrderLines_butDoesNotClearCart() {
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

        int created = cartService.checkout(10L, address);

        assertEquals(2, created);
        verify(orderRepository).save(any(Order.class));
        verify(orderLineRepository, times(2)).save(any(OrderLine.class));
        verify(cartItemRepository, never()).delete(any(CartItem.class));
        assertFalse(cart.getItems().isEmpty());
    }

    /**
     * Verifies that {@link CartService#calculateTotal(Long)} returns the sum of
     * price × quantity for each cart item.
     */
    @Test
    void calculateTotal_sumsPriceTimesQuantity() {
        CartItem ci1 = new CartItem();
        ci1.setBook(book1);
        ci1.setQuantity(2); // 20

        CartItem ci2 = new CartItem();
        ci2.setBook(book2);
        ci2.setQuantity(1); // 15

        cart.addItem(ci1);
        cart.addItem(ci2);

        when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));

        BigDecimal total = cartService.calculateTotal(10L);
        assertEquals(BigDecimal.valueOf(35.0), total);
    }
}