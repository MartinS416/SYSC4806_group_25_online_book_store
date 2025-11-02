package com.example.demo;

import com.example.demo.BookRepository;
import com.example.demo.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@SessionAttributes("cart")
public class CartController {

    private final BookRepository books;
    private final CartService cartService;

    public CartController(BookRepository books, CartService cartService) {
        this.books = books;
        this.cartService = cartService;
    }

    // Creates a cart session if it doesn’t exist
    @ModelAttribute("cart")
    public Map<Long, Integer> cart() {
        return new HashMap<>();
    }

    // Show all books
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", books.findAll());
        return "books";
    }

    // Add a book to cart
    @PostMapping("/cart/add/{id}")
    public String add(@PathVariable Long id, @ModelAttribute("cart") Map<Long, Integer> cart) {
        cartService.add(cart, id);
        return "redirect:/cart";
    }

    // Remove or decrement a book from cart
    @PostMapping("/cart/remove/{id}")
    public String remove(@PathVariable Long id, @ModelAttribute("cart") Map<Long, Integer> cart) {
        cartService.remove(cart, id);
        return "redirect:/cart";
    }

    // View cart page
    @GetMapping("/cart")
    public String cart(@ModelAttribute("cart") Map<Long, Integer> cart, Model model) {
        model.addAttribute("items", cartService.detailed(cart));
        model.addAttribute("total", cartService.total(cart));
        return "cart";
    }

    // Clear the cart
    @PostMapping("/cart/clear")
    public String clear(SessionStatus status) {
        status.setComplete();
        return "redirect:/books";
    }
}