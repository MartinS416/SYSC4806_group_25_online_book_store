package com.example.demo.controller;

import com.example.demo.model.Address;
import com.example.demo.model.Book;
import com.example.demo.model.Cart;
import com.example.demo.model.Customer;
import com.example.demo.repository.AddressRepository;
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

        Book fantasyBook = new Book(); //remove later
        fantasyBook.setTitle("The Hobbit");
        fantasyBook.setAuthor("J.R.R. Tolkien");
        fantasyBook.setCategory("Fantasy");
        fantasyBook.setPrice(new BigDecimal("14.99"));
        fantasyBook.setStock(10);

        books.save(fantasyBook);
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
        Book book = books.getReferenceById(id);
        if(book.getStock() > 0){
            book.setStock(book.getStock() - 1);
        } else {
            return "redirect:/shop";
        }
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
    public String checkout(@AuthenticationPrincipal CustomUserDetails principal, SessionStatus status,
                           @RequestParam("cardNumber") String cardNumber, @RequestParam("expiry") String expiry,
                           @RequestParam("cvv") String cvv, @RequestParam("firstName") String fname,
                           @RequestParam("lastName") String lname,  @RequestParam("street") String street,
                           @RequestParam("unit") String unit,  @RequestParam("city") String city,
                           @RequestParam("region") String region,  @RequestParam("country") String country,
                           @RequestParam("postal") String postal) {
        if (principal == null) return "redirect:/login";
        Customer customer = customerRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + principal.getUsername()));
        if (cartService.checkCard(cardNumber, expiry, cvv)) {
            Cart cart = cartService.findOrCreateCartForCustomer(customer.getId());
            Address billing = new Address();
            billing.setFirstName(fname);
            billing.setLastName(lname);
            billing.setStreet(street);
            billing.setCity(city);
            billing.setRegion(region);
            billing.setCountry(country);
            billing.setPostcode(postal);
            billing.setCustomer(customer);
            if (!unit.isEmpty()){
                billing.setUnit(unit);
            }
            cartService.checkout(cart.getId(), billing);
            return "redirect:/shop?paid=true";
        } else {
            return "redirect:/cart?paid=false";
        }
    }

    @PostMapping("/cart/pay")
    public String pay(@ModelAttribute("cart") Map<Long, Integer> cart, SessionStatus status,
                      @RequestParam("cardNumber") String cardNumber, @RequestParam("expiry") String expiry,
                      @RequestParam("cvv") String cvv, @RequestParam("firstName") String fname,
                      @RequestParam("lastName") String lname,  @RequestParam("street") String street,
                      @RequestParam("unit") String unit,  @RequestParam("city") String city,
                      @RequestParam("region") String region,  @RequestParam("country") String country) {
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