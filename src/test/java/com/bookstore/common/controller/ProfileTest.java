package com.bookstore.common.controller;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepo;

    private Customer user;

    @BeforeEach
    void setUp() {
        customerRepo.deleteAll();

        user = new Customer();
        user.setEmail("test@example.com");
        user.setUsername("test@example.com");
        user.setPassword("$2a$12$10mKHpl7mhpYLQ9djrM2EuOQrLrcmCzXifHYUu.96OzyEI9ZuUU/e");
        user.setPhone("1112223333");
        user.setFirstName("Test");
        user.setLastName("User");

        customerRepo.save(user);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testUpdatePhoneNumber() throws Exception {

        mockMvc.perform(post("/profile/update")
                        .param("phone", "5556667777"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attribute("success", "Profile updated successfully!"));

        Customer updated = customerRepo.findByEmail("test@example.com").orElseThrow();
        assert(updated.getPhone().equals("5556667777"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testChangePasswordSuccess() throws Exception {
        user.setPassword("$2a$12$10mKHpl7mhpYLQ9djrM2EuOQrLrcmCzXifHYUu.96OzyEI9ZuUU/e");
        customerRepo.save(user);

        mockMvc.perform(post("/profile/change-password")
                        .param("oldPassword", "oldpass")
                        .param("newPassword", "newpass123")
                        .param("confirmPassword", "newpass123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attribute("success", "Password changed successfully!"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testChangePasswordWrongOldPassword() throws Exception {

        mockMvc.perform(post("/profile/change-password")
                        .param("oldPassword", "WRONG")
                        .param("newPassword", "newpass123")
                        .param("confirmPassword", "newpass123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Old password is incorrect."));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testChangePasswordMismatch() throws Exception {

        mockMvc.perform(post("/profile/change-password")
                        .param("oldPassword", "oldpass")
                        .param("newPassword", "abc123")
                        .param("confirmPassword", "DIFFERENT"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "New passwords do not match."));
    }
}

