package com.bookstore.pos.controller;

import com.bookstore.pos.model.Order;
import com.bookstore.demo.model.Address;
import com.bookstore.demo.model.Book;
import com.bookstore.pos.model.Cart;
import com.bookstore.demo.model.Customer;
import com.bookstore.demo.repository.AddressRepository;
import com.bookstore.demo.repository.BookRepository;
import com.bookstore.demo.repository.CustomerRepository;
import com.bookstore.demo.security.CustomUserDetails;
import com.bookstore.pos.service.CartService;
import com.bookstore.pos.service.PaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Web controller for cart-related operations backed by persistent Cart entities.
 * <p>
 * Endpoints in this controller allow users to browse books, manage cart items,
 * and perform checkout with basic card validation.
 */
@Controller
public class CartController {

    private final BookRepository books;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public CartController(BookRepository books,
                          CartService cartService,
                          PaymentService paymentService,
                          CustomerRepository customerRepository,
                          AddressRepository addressRepository) {
        this.books = books;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    /**
     * Displays the main shop view with all available books.
     *
     * @param model the model used to supply view attributes
     * @return the logical view name for the shop page
     */
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", books.findAll());
        return "shop";
    }

    /**
     * Adds a single copy of a book to the authenticated customer's cart.
     * <p>
     * The method also decrements the book stock by one if stock is available.
     *
     * @param id        the identifier of the book to add
     * @param principal the authenticated user details, or {@code null} if not logged in
     * @return a redirect to the shop view or login page when unauthenticated
     */
    @PostMapping("/cart/add/{id}")
    public String add(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return "redirect:/login";

        Customer customer = customerRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + principal.getUsername()));
        Book book = books.getReferenceById(id);
        if (book.getStock() > 0) {
            book.setStock(book.getStock() - 1);
        } else {
            return "redirect:/shop";
        }
        Cart cart = cartService.findOrCreateCartForCustomer(customer.getId());
        cartService.addItem(cart.getId(), id, 1);
        return "redirect:/shop";
    }

    /**
     * Removes a single copy of a book from the authenticated customer's cart.
     * <p>
     * When the quantity for the book reaches zero, the corresponding cart item is removed.
     *
     * @param id        the identifier of the book to remove
     * @param principal the authenticated user details, or {@code null} if not logged in
     * @return a redirect to the cart view or login page when unauthenticated
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
     * Displays the current authenticated customer's cart contents and total.
     *
     * @param principal the authenticated user details, or {@code null} if not logged in
     * @param model     the model used to supply items and total to the view
     * @return the logical view name for the cart page or a redirect to login
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
     * Performs checkout for the authenticated customer's cart.
     * <p>
     * This method validates the card expiry, verifies the cart is not empty,
     * builds a billing address, creates an {@link Order}
     * and its order lines via {@link CartService#checkout(Long, Address)}, and
     * clears the cart only if the order creation succeeds.
     *
     * @param principal the authenticated user details, or {@code null} if not logged in
     * @param status    the session status used to complete the web session after checkout
     * @param cardNumber the payment card number (basic validation only)
     * @param expiry     the card expiry in {@code MM/yy} format
     * @param cvv        the card CVV (3–4 digits)
     * @param fname      the billing first name
     * @param lname      the billing last name
     * @param street     the billing street address
     * @param unit       the billing unit or apartment (optional)
     * @param city       the billing city
     * @param region     the billing region or state
     * @param country    the billing country
     * @param postal     the billing postal code
     * @return a redirect to the shop page with a success flag, or back to the cart with an error flag
     */
    @PostMapping("/cart/checkout")
    public String checkout(@AuthenticationPrincipal CustomUserDetails principal,
                           SessionStatus status,
                           @RequestParam("cardNumber") String cardNumber,
                           @RequestParam("expiry") String expiry,
                           @RequestParam("cvv") String cvv,
                           @RequestParam("firstName") String fname,
                           @RequestParam("lastName") String lname,
                           @RequestParam("street") String street,
                           @RequestParam("unit") String unit,
                           @RequestParam("city") String city,
                           @RequestParam("region") String region,
                           @RequestParam("country") String country,
                           @RequestParam("postal") String postal) {

        if (principal == null) return "redirect:/login";

        try {
            Customer customer = customerRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Customer not found: " + principal.getUsername()));

            Cart cart = cartService.findOrCreateCartForCustomer(customer.getId());

            if (!cartService.checkCard(cardNumber, expiry, cvv)) {
                return "redirect:/cart?error=card_expired";
            }

            Map<?, Integer> items = cartService.getDetailedCart(cart.getId());
            if (items == null || items.isEmpty()) {
                return "redirect:/cart?error=empty_cart";
            }

            Address billing = new Address();
            billing.setFirstName(fname);
            billing.setLastName(lname);
            billing.setStreet(street);
            billing.setCity(city);
            billing.setRegion(region);
            billing.setCountry(country);
            billing.setPostcode(postal);
            billing.setCustomer(customer);
            if (!unit.isEmpty()) {
                billing.setUnit(unit);
            }

            int orderLinesCreated = cartService.checkout(cart.getId(), billing);

            if (orderLinesCreated > 0) {
                cartService.clearCart(cart.getId());
                status.setComplete();
                return "redirect:/shop?paid=true";
            } else {
                return "redirect:/cart?error=checkout_failed";
            }
        } catch (IllegalStateException e) {
            return "redirect:/cart?error=" + e.getMessage();
        } catch (Exception e) {
            return "redirect:/cart?error=checkout_error";
        }
    }

    /**
     * Clears all items from the authenticated customer's cart.
     *
     * @param principal the authenticated user details, or {@code null} if not logged in
     * @return a redirect to the shop page or login page when unauthenticated
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