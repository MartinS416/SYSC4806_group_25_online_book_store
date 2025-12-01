package com.bookstore.demo.service;


import com.bookstore.demo.model.Customer;
import com.bookstore.demo.repository.AddressRepository;
import com.bookstore.demo.repository.CustomerRepository;
import com.bookstore.pos.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginTest {


    @Autowired
    MockMvc mvc;

    @Autowired
    CustomerRepository repo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    OrderRepository orderRepo;     // add this repo
    @Autowired
    AddressRepository addressRepo; // add this repo

    @BeforeEach
    void setup() {
        System.out.println("=== CLEANING DB ===");
        orderRepo.deleteAll();       // delete child records first
        addressRepo.deleteAll();     // then addresses
        repo.deleteAll();            // then customers


        Customer c = new Customer();
        c.setEmail("test@example.com");
        c.setUsername("TestUser");
        c.setPassword(encoder.encode("password123")); // must match test login
        repo.save(c);
    }

    /**
     * Tests the login, see's if it redirects to the proper page, shows authentication works
     * @throws Exception
     */
    @Test
    void login_success() throws Exception {
        mvc.perform(post("/login")
                        .param("username", "test@example.com") // must match usernameParameter
                        .param("password", "password123"))     // must match encoded password
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    /**
     * Tests password encoding
     */
    @Test
    void passwordEncoder_hashesCorrectly() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("password123");

        assertNotEquals("password123", encoded);
        assertTrue(encoder.matches("password123", encoded));
    }

    /**
     * tests if shop is accessable without authentication
     * @throws Exception
     */
    @Test
    void shop_isAccessibleWithoutLogin() throws Exception {
        mvc.perform(get("/shop"))
                .andExpect(status().isOk());
    }

    /**
     * tests if cart is available without being authenticated. Should redirect to login page.
     * @throws Exception
     */
    @Test
    void cart_redirectsIfNotLoggedIn() throws Exception {
        mvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }


}

