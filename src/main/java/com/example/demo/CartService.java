package com.example.demo;

import com.example.demo.Book;
import com.example.demo.BookRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDate;

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
    public boolean processPayment(Map<Long, Integer> cart, String cardNumber, String expiry, String cvv) {
        if(checkCard(cardNumber,expiry,cvv)) {
            System.out.println("not expired");
            cart.forEach((bookId, qty) -> {
                books.findById(bookId).ifPresent(book -> {
                    int stock = Math.max(0, book.getStock() - qty);
                    book.setStock(stock);
                    books.save(book);
                });
            });
            return true;
        } else {
            System.out.println("card expired");
            return false;
        }
    }

    public boolean checkCard(String cardNumber, String expiry, String cvv){
        //currently just checks if the card is expired. other checks could be added later.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        String currentDate = LocalDate.now().format(formatter);
        return Integer.parseInt(currentDate.split("/")[1]) <= Integer.parseInt(expiry.split("/")[1]) &&
                (Integer.parseInt(currentDate.split("/")[1]) != Integer.parseInt(expiry.split("/")[1]) ||
                        Integer.parseInt(currentDate.split("/")[0]) <= Integer.parseInt(expiry.split("/")[0]));
    }
}