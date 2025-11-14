package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.model.Customer;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;

    public CartController(CartRepository cartRepository, BookRepository bookRepository, CartService cartService) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.cartService = cartService;
    }

    /**
     * List all books.
     *
     * @param model A container used to pass data to the view template.
     *              The list of books fetched from the repository is added
     *              as an attribute to the model with the key "books".
     * @return The name of the view template to render.
     */
    @GetMapping("/shop")
    public String listBooks(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "shop";
    }

    /**
     * Add a book to the cart.
     *
     * @param bookId The ID of the book to add.
     * @param customerId The ID of the customer.
     * @return New cart view.
     */
    @PostMapping("/add/{bookId}")
    public String addBookToCart(@PathVariable Long bookId, @RequestParam Long customerId) {
        Cart cart = cartRepository.findByCustomerIdAndActiveTrue(customerId)
                .orElseGet(() -> {
                    Cart c = new Cart(new Customer()); // Fetch a customer properly in a real scenario
                    cartRepository.save(c);
                    return c;
                });

        cartService.addItem(cart, bookId, 1);
        return "redirect:/cart/view?customerId=" + customerId;
    }

    /**
     * Remove a book from the cart.
     *
     * @param bookId The ID of the book to remove.
     * @param customerId The ID of the customer.
     * @return New cart view.
     */
    @PostMapping("/remove/{bookId}")
    public String removeBookFromCart(@PathVariable Long bookId, @RequestParam Long customerId) {
        Cart cart = cartRepository.findByCustomerIdAndActiveTrue(customerId)
                .orElseThrow(() -> new IllegalStateException("Cart not found"));

        cartService.removeItem(cart, bookId, 1);
        return "redirect:/cart/view?customerId=" + customerId;
    }

    /**
     * View cart.
     *
     * @param customerId The ID of the customer.
     * @param model A container used to pass data to the view template.
     *              The list of books fetched from the repository is added
     *              as an attribute to the model with the key "cart".
     * @return The name of the view template to render.
     */
    @GetMapping("/view")
    public String viewCart(@RequestParam Long customerId, Model model) {
        Cart cart = cartRepository.findByCustomerIdAndActiveTrue(customerId)
                .orElseThrow(() -> new IllegalStateException("Cart not found"));

        model.addAttribute("items", cartService.detailed(cart));
        model.addAttribute("total", cartService.total(cart));
        return "cart";
    }

    /**
     * Checkout cart, process payment, update stock.
     *
     * @param customerId The ID of the customer.
     * @return New cart view with a paid flag set as confirmation of payment.
     */
    @PostMapping("/checkout")
    public String checkout(@RequestParam Long customerId) {
        Cart cart = cartRepository.findByCustomerIdAndActiveTrue(customerId)
                .orElseThrow(() -> new IllegalStateException("Cart not found"));

        cartService.checkout(cart.getId());
        return "redirect:/cart/view?customerId=" + customerId + "&paid=true";
    }

    // Clear cart

    /**
     * Clear the cart.
     *
     * @param customerId The ID of the customer.
     * @return New cart view with the cart cleared.
     */
    @PostMapping("/clear")
    public String clearCart(@RequestParam Long customerId) {
        Cart cart = cartRepository.findByCustomerIdAndActiveTrue(customerId)
                .orElseThrow(() -> new IllegalStateException("Cart not found"));

        cart.getItems().forEach(item -> {
            item.setCart(null);
        });
        cart.getItems().clear();
        cart.deactivate();
        cartRepository.save(cart);

        return "redirect:/cart/view?customerId=" + customerId;
    }
}