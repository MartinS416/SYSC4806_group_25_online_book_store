package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.model.Customer;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controller using a repository-backed CartService. No session Map-based methods here.
 */
@Controller
public class CartController {

    private final BookRepository books;
    private final CartService cartService;
    private final CustomerRepository customerRepository;

    public CartController(BookRepository books, CartService cartService, CustomerRepository customerRepository) {
        this.books = books;
        this.cartService = cartService;
        this.customerRepository = customerRepository;
    }

    /**
     * List all books.
     *
     * @param model A container used to pass data to the view template.
     *              The list of books fetched from the repository is added
     *              as an attribute to the model with the key "books".
     * @return The name of the view template to render.
     */
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", books.findAll());
        return "shop";
    }

    /**
     * Add a book to the cart.
     *
     * @param id The ID of the book to add.
     * @param principal The logged-in customer.
     * @return New cart view.
     */
    @PostMapping("/cart/add/{id}")
    public String add(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return "redirect:/login";

        Customer customer = customerRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + principal.getUsername()));

        Cart cart = cartService.findOrCreateCartForCustomer(customer.getId());
        cartService.addItem(cart.getId(), id, 1);
        return "redirect:/shop";
    }

    /**
     * Remove a book from the cart.
     *
     * @param id The ID of the book to remove.
     * @param principal The logged-in customer.
     * @return New cart view.
     */
    @PostMapping("/cart/remove/{id}")
    public String remove(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return "redirect:/login";

        Customer customer = customerRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + principal.getUsername()));

        Cart cart = cartService.findOrCreateCartForCustomer(customer.getId());
        cartService.removeItem(cart.getId(), id, 1);
        return "redirect:/cart";
    }

    /**
     * View cart.
     *
     * @param principal The logged-in customer.
     * @param model A container used to pass data to the view template.
     *              The list of books fetched from the repository is added
     *              as an attribute to the model with the key "cart".
     * @return The name of the view template to render.
     */
    @GetMapping("/cart")
    public String cart(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        if (principal == null) return "redirect:/login";

        Customer customer = customerRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + principal.getUsername()));

        Cart cart = cartService.findOrCreateCartForCustomer(customer.getId());
        Map<?, Integer> items = cartService.getDetailedCart(cart.getId());
        BigDecimal total = cartService.calculateTotal(cart.getId());

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "cart";
    }

    /**
     * Checkout cart, process payment, update stock.
     *
     * @param principal The logged-in customer.
     * @return New cart view with a paid flag set as confirmation of payment.
     */
    @PostMapping("/cart/checkout")
    public String checkout(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return "redirect:/login";

        Customer customer = customerRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + principal.getUsername()));

        Cart cart = cartService.findOrCreateCartForCustomer(customer.getId());
        cartService.checkout(cart.getId());
        return "redirect:/shop?paid=true";
    }

    @PostMapping("/cart/pay")
    public String pay(@ModelAttribute("cart") Map<Long, Integer> cart, SessionStatus status,
                      @RequestParam("cardNumber") String cardNumber, @RequestParam("expiry") String expiry,
                      @RequestParam("cvv") String cvv) {
        if (cartService.processPayment(cart, cardNumber, expiry, cvv)) {
            status.setComplete();
            return "redirect:/shop?paid=true";
        } else {
            return "redirect:/cart?paid=false";
        }
    }

    /**
     * Clear the cart.
     *
     * @param principal The logged-in customer.
     * @return New cart view with the cart cleared.
     */
    @PostMapping("/cart/clear")
    public String clear(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return "redirect:/login";

        Customer customer = customerRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + principal.getUsername()));

        Cart cart = cartService.findOrCreateCartForCustomer(customer.getId());
        cartService.clearCart(cart.getId());
        return "redirect:/shop";
    }
}