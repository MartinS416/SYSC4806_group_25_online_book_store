package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Cart;
import com.example.demo.model.Customer;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * WebMvc tests for CartController (repository-backed).
 */
@WebMvcTest(controllers = com.example.demo.controller.CartController.class)
class CartControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    BookRepository bookRepository;

    @MockitoBean
    CartService cartService;

    @MockitoBean
    CustomerRepository customerRepository;

    private CustomUserDetails principal;
    private Cart cart;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        ReflectionTestUtils.setField(customer, "id", 42L);
        customer.setEmail("test@example.com");
        customer.setUsername("TestUser");

        principal = new CustomUserDetails(customer);

        cart = new Cart(customer);
        ReflectionTestUtils.setField(cart, "id", 10L);

        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        book.setStock(10);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(cartService.findOrCreateCartForCustomer(customer.getId())).thenReturn(cart);
        when(bookRepository.getReferenceById(1L)).thenReturn(book);
    }

    @Test
    void add_callsAddItemAndRedirects() throws Exception {
        mvc.perform(post("/cart/add/{id}", 1L)
                        .with(user(principal))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop"));

        verify(cartService, times(1)).addItem(cart.getId(), 1L, 1);
    }

    @Test
    void remove_callsRemoveItemAndRedirects() throws Exception {
        mvc.perform(post("/cart/remove/{id}", 2L)
                        .with(user(principal))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).removeItem(cart.getId(), 2L, 1);
    }

    @Test
    void viewCart_populatesModelAndReturnsView() throws Exception {
        Book b = new Book();
        b.setId(1L);
        b.setTitle("Test Book");
        b.setPrice(BigDecimal.valueOf(12.5));

        Map<Book, Integer> items = new LinkedHashMap<>();
        items.put(b, 2);

        when(cartService.getDetailedCart(cart.getId())).thenReturn(items);
        when(cartService.calculateTotal(cart.getId())).thenReturn(BigDecimal.valueOf(25.0));

        mvc.perform(get("/cart")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("items", items))
                .andExpect(model().attribute("total", comparesEqualTo(BigDecimal.valueOf(25.0))));

        verify(cartService).getDetailedCart(cart.getId());
        verify(cartService).calculateTotal(cart.getId());
    }

    @Test
    void clear_callsClearAndRedirects() throws Exception {
        mvc.perform(post("/cart/clear")
                        .with(user(principal))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop"));

        verify(cartService).clearCart(cart.getId());
    }

    /**
     * TODO fix this test!!
     * @Test
     *     void pay_callsCheckoutAndRedirectsPaid() throws Exception {
     *         // ensure the service is stubbed so the controller won't throw
     *         when(cartService.checkout(cart.getId())).thenReturn(1);
     *
     *         MvcResult result = mvc.perform(post("/cart/pay")
     *                         .with(user(principal))
     *                         .with(csrf()))
     *                 .andReturn();
     *
     *         int status = result.getResponse().getStatus();
     *         if (status < 300 || status >= 400) {
     *             // print for debugging to see why we got a client error
     *             System.err.println("PAY endpoint response status: " + status);
     *             System.err.println("PAY endpoint response body: " + result.getResponse().getContentAsString());
     *         }
     *
     *         assertTrue(status >= 300 && status < 400, "Expected redirect status but got: " + status);
     *         verify(cartService).checkout(cart.getId());
     *     }
     * @throws Exception
     */
}