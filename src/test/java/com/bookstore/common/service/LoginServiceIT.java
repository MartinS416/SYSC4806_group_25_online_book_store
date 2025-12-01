package com.bookstore.common.service;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.AddressRepository;
import com.bookstore.common.repository.CustomerRepository;
import com.bookstore.pos.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration tests for login and basic security behavior.
 *
 * <h2>Test Category:</h2> Integration Tests (IT) – Web/MVC + Security.
 * <h2>Scope:</h2>
 * <ul>
 *   <li>Form login flow and redirect behavior.</li>
 *   <li>Password encoding sanity check.</li>
 *   <li>Anonymous access to public endpoints and protection of /cart.</li>
 * </ul>
 *
 * <h2>Dependencies:</h2>
 * Full Spring Boot context, security configuration, {@link MockMvc},
 * {@link CustomerRepository}, {@link OrderRepository}, {@link AddressRepository}, {@link PasswordEncoder}.
 *
 * @author Skach, Martin; Lavji, Fareen
 * @version 3.0
 * @since 2025.11.16
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Login Integration Tests")
public class LoginServiceIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    CustomerRepository repo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    AddressRepository addressRepo;

    @BeforeEach
    void setup() {
        orderRepo.deleteAll();
        addressRepo.deleteAll();
        repo.deleteAll();

        Customer c = new Customer();
        c.setEmail("test@example.com");
        c.setUsername("TestUser");
        c.setPassword(encoder.encode("password123"));
        repo.save(c);
    }

    /**
     * Test: successful login redirects to home page.
     */
    @Test
    @DisplayName("login_success redirects to /")
    void login_success() throws Exception {
        mvc.perform(post("/login")
                        .param("username", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    /**
     * Test: password encoder hashes and verifies passwords correctly.
     */
    @Test
    @DisplayName("passwordEncoder hashes and matches")
    void passwordEncoder_hashesCorrectly() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("password123");

        assertNotEquals("password123", encoded);
        assertTrue(encoder.matches("password123", encoded));
    }

    /**
     * Test: /shop is accessible without authentication.
     */
    @Test
    @DisplayName("shop is accessible without login")
    void shop_isAccessibleWithoutLogin() throws Exception {
        mvc.perform(get("/shop"))
                .andExpect(status().isOk());
    }

    /**
     * Test: /cart redirects unauthenticated users to login.
     */
    @Test
    @DisplayName("cart redirects to login when not authenticated")
    void cart_redirectsIfNotLoggedIn() throws Exception {
        mvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}