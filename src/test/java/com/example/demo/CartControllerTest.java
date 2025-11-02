package com.example.demo;

import com.example.demo.Book;
import com.example.demo.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private BookRepository repo;

    private Long bookId;

    @BeforeEach
    void seed() {
        repo.deleteAll();
        bookId = repo.save(new Book("Test Book", "Author", 25.0)).getId();
    }

    @Test
    void addBookAndShowCart() throws Exception {
        mvc.perform(post("/cart/add/" + bookId))
                .andExpect(status().is3xxRedirection());

        mvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items", "total"))
                .andExpect(view().name("cart"));
    }

    @Test
    void showBooksPage() throws Exception {
        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("books"))
                .andExpect(view().name("books"));
    }
}