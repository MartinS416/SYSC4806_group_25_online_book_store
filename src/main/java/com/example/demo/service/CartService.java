package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CartService {

    private final BookRepository books;

    public CartService(BookRepository books) {
        this.books = books;
    }

    // Adds a book by ID; increments quantity if already exists
    public Map<Long, Integer> add(Map<Long, Integer> cart, Long bookId) {
        books.findById(bookId).orElseThrow();
        cart.merge(bookId, 1, Integer::sum);
        return cart;
    }

    // Removes or decrements a book by ID
    public Map<Long, Integer> remove(Map<Long, Integer> cart, Long bookId) {
        cart.computeIfPresent(bookId, (k, v) -> v > 1 ? v - 1 : null);
        return cart;
    }

    // Calculates total price
    public double total(Map<Long, Integer> cart) {
        return cart.entrySet().stream()
                .mapToDouble(e ->
                        books.findById(e.getKey()).orElseThrow().getPrice() * e.getValue())
                .sum();
    }

    // Converts ID-quantity pairs to Book-quantity pairs
    public Map<Book, Integer> detailed(Map<Long, Integer> cart) {
        Map<Book, Integer> result = new LinkedHashMap<>();
        cart.forEach((id, qty) ->
                result.put(books.findById(id).orElseThrow(), qty));
        return result;
    }

    //Simple process payment method, to be changed later, just updates the stock of the book and clears the session to empty the cart.
    public void processPayment(Map<Long, Integer> cart) {
        cart.forEach((bookId,qty)->{
            books.findById(bookId).ifPresent(book-> {
                int stock = Math.max(0, book.getStock() -qty);
                book.setStock(stock);
                books.save(book);
            });
        });
    }
}