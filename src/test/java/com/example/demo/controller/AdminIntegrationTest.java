package com.example.demo.integration;

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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
public class AdminIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired CustomerRepository customerRepo;
    @Autowired PasswordEncoder encoder;

    Customer adminUser;

    @BeforeEach
    void setup() {
        customerRepo.deleteAll();

        adminUser = new Customer();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(encoder.encode("adminpass"));
        adminUser.setRole("ADMIN");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPhone("1112223333");

        customerRepo.save(adminUser);
    }

    @Test
    void admin_canLogin() throws Exception {
        mvc.perform(post("/login")
                        .param("username", "admin@example.com")
                        .param("password", "adminpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void nonAdmin_cannotAccessAdminPages() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void admin_canAccessDashboard() throws Exception {
        mvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Dashboard")));
    }

    @Test
    void admin_canViewBookList() throws Exception {
        mvc.perform(get("/admin/books").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Books")));
    }

    @Test
    void admin_canCreateBook() throws Exception {
        mvc.perform(post("/admin/books")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("title", "Test Book")
                        .param("author", "Tester")
                        .param("category", "Fiction")
                        .param("price", "19.99")
                        .param("stock", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books"));
    }

    @Test
    void admin_canViewCustomerList() throws Exception {
        mvc.perform(get("/admin/customers").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Customers")));
    }

    @Test
    void admin_canCreateCustomer() throws Exception {
        mvc.perform(post("/admin/customers")
                        .with(user("admin").roles("ADMIN"))
                        .param("username", "newUser")
                        .param("email", "new@example.com")
                        .param("firstName", "New")
                        .param("lastName", "User")
                        .param("phone", "1234567890")
                        .param("role", "USER")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/customers"));
    }

}
