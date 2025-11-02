package com.example.demo;

import com.example.demo.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repo;

    @Test
    void saveAndFindBook() {
        Book book = new Book("Clean Code", "Robert C. Martin", 38.99);
        Book saved = repo.save(book);

        assertNotNull(saved.getId());
        assertTrue(repo.findById(saved.getId()).isPresent());
        assertEquals("Clean Code",
                repo.findById(saved.getId()).orElseThrow().getTitle());
    }
}
