package com.example.demo;

import com.example.demo.Book;
import com.example.demo.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartServiceTest {

    @Autowired
    private BookRepository repo;

    private CartService cartService;
    private Map<Long, Integer> cart;

    @BeforeEach
    void setup() {
        repo.save(new Book("Book A", "Author A", 10.0));
        repo.save(new Book("Book B", "Author B", 15.0));
        cartService = new CartService(repo);
        cart = new HashMap<>();
    }

    @Test
    void addAndTotalWorks() {
        Long id1 = repo.findAll().get(0).getId();
        Long id2 = repo.findAll().get(1).getId();

        cartService.add(cart, id1);
        cartService.add(cart, id1);
        cartService.add(cart, id2);

        assertEquals(2, cart.get(id1));
        assertEquals(1, cart.get(id2));
        assertEquals(35.0, cartService.total(cart), 1e-6);
    }

    @Test
    void removeReducesQuantityAndRemoves() {
        Long id = repo.findAll().get(0).getId();

        cartService.add(cart, id);
        cartService.add(cart, id);

        cartService.remove(cart, id);
        assertEquals(1, cart.get(id));

        cartService.remove(cart, id);
        assertFalse(cart.containsKey(id));
    }
}