package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
public class loginTest {

    @Autowired MockMvc mvc;
    @Autowired CustomerRepository repo;
    @Autowired PasswordEncoder encoder;

    @BeforeEach
    void setup() {
        System.out.println("=== CLEANING DB ===");
        repo.deleteAll();

        Customer c = new Customer();
        c.setEmail("test@example.com");
        c.setUsername("TestUser");
        c.setPassword(encoder.encode("password123"));
        c.setRole("USER");
        c.setFirstName("Test");
        c.setLastName("User");
        c.setPhone("1234567890");

        repo.save(c);
    }

    @Test
    void login_success() throws Exception {
        System.out.println("=== login_success ===");

        mvc.perform(post("/login")
                        .param("username", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void passwordEncoder_hashesCorrectly() {
        System.out.println("=== passwordEncoder_hashesCorrectly ===");

        String encoded = encoder.encode("password123");

        System.out.println("Encoded password: " + encoded);

        assertNotEquals("password123", encoded);
        assertTrue(encoder.matches("password123", encoded));
    }

    @Test
    void shop_isAccessibleWithoutLogin() throws Exception {
        System.out.println("=== shop_isAccessibleWithoutLogin ===");

        mvc.perform(get("/shop"))
                .andExpect(status().isOk());
    }

    @Test
    void cart_redirectsIfNotLoggedIn() throws Exception {
        System.out.println("=== cart_redirectsIfNotLoggedIn ===");

        mvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
