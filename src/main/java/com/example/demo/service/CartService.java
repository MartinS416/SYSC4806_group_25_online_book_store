package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Repository-backed cart service.
 * - addItem/removeItem validate negative quantities and persist via repositories
 * - checkout returns the number of created order lines and throws an error
 *   message when the cart is empty
 */
@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final CustomerRepository customerRepository;

    /**
     * Constructor.
     *
     * @param cartRepository Live Repo of Carts.
     * @param cartItemRepository Live Repo of CartItems.
     * @param bookRepository Live Repo of Books.
     * @param orderLineRepository Live Repo of OrderLines to be attached to Orders.
     */
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       OrderRepository orderRepository,
                       OrderLineRepository orderLineRepository,
                       CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Find an active cart for the customer or create a new one.
     * Requires CartRepository to expose:
     *      Optional<Cart> findByCustomerIdAndActiveTrue(Long customerId)
     *
     * @param customerId The ID of the customer.
     * @return The cart for the customer.
     */
    @Transactional
    public Cart findOrCreateCartForCustomer(Long customerId) {
        return cartRepository.findByCustomerIdAndActiveTrue(customerId)
                .orElseGet(() -> {
                    Customer c = customerRepository.findById(customerId)
                            .orElseThrow(() -> new NoSuchElementException("Customer not found: " + customerId));
                    Cart cart = new Cart(c);
                    cart.activate();
                    return cartRepository.save(cart);
                });
    }

    /**
     * Add the quantity of the given book to the cart (create or increment CartItem).
     *
     * @param cartId   The ID associated with the cart to add item to.
     * @param bookId   The ID of the book to add.
     * @param quantity The quantity of the book to add.
     * @return The CartItem created or updated.
     * @throws IllegalArgumentException for negative quantities.
     */
    @Transactional
    public CartItem addItem(Long cartId, Long bookId, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found: " + cartId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found: " + bookId));

        Optional<CartItem> existingOpt = cart.getItems().stream()
                .filter(ci -> ci.getBook() != null && bookId.equals(ci.getBook().getId()))
                .findFirst();

        CartItem toSave;
        if (existingOpt.isPresent()) {
            CartItem existing = existingOpt.get();
            existing.setQuantity(existing.getQuantity() + quantity);
            toSave = existing;
        } else {
            CartItem ci = new CartItem();
            ci.setBook(book);
            ci.setCart(cart);
            ci.setQuantity(quantity);
            cart.addItem(ci);
            toSave = ci;
        }

        CartItem saved = cartItemRepository.save(toSave);
        cartRepository.save(cart); // persist relationship changes
        return saved;
    }

    /**
     * Remove item from the cart. If quantity removes all units, the CartItem is deleted.
     *
     * @param cartId The ID associated with the cart to remove item from.
     * @param bookId The ID of the book to remove.
     * @param quantity The quantity of the book to remove.
     * @throws IllegalArgumentException for negative quantities.
     */
    @Transactional
    public void removeItem(Long cartId, Long bookId, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found: " + cartId));

        CartItem existing = cart.getItems().stream()
                .filter(ci -> ci.getBook() != null && bookId.equals(ci.getBook().getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cart item not found for book: " + bookId));

        if (quantity >= existing.getQuantity()) {
            cart.removeItem(existing);
            cartItemRepository.delete(existing);
        } else {
            existing.setQuantity(existing.getQuantity() - quantity);
            cartItemRepository.save(existing);
        }

        cartRepository.save(cart);
    }

    /**
     * Keep template ordering stable (like previous detailed method).
     *
     * @param cartId The ID associated with the cart to calculate the total price of.
     * @return LinkedHashMap<Book, Integer> of books and quantities in cart.
     */
    @Transactional
    public Map<Book, Integer> getDetailedCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found: " + cartId));
        LinkedHashMap<Book, Integer> result = new LinkedHashMap<>();
        cart.getItems().forEach(ci -> result.put(ci.getBook(), ci.getQuantity()));
        return result;
    }

    /**
     * Calculate the total price of the cart.
     *
     * @param cartId The ID associated with the cart to calculate the total price of.
     * @return The total price of the cart.
     */
    @Transactional
    public BigDecimal calculateTotal(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found: " + cartId));
        return BigDecimal.valueOf(cart.getItems().stream()
                .mapToDouble(ci -> ci.getBook().getPrice().doubleValue() * ci.getQuantity())
                .sum());
    }

    /**
     * Clears the cart (removes persisted CartItems).
     *
     * @param cartId The ID associated with the cart to clear.
     */
    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found: " + cartId));
        List<CartItem> copy = new ArrayList<>(cart.getItems());
        for (CartItem ci : copy) {
            cart.removeItem(ci);
            cartItemRepository.delete(ci);
        }
        cartRepository.save(cart);
    }

    /**
     * Check out the cart: create an Order and OrderLines from CartItems, clear the cart.
     *
     * @param cartId The ID associated with the cart to calculate the total price of.
     * @return The number of order lines created.
     * @throws IllegalStateException if the cart is empty.
     */
    @Transactional
    public int checkout(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found: " + cartId));

        List<CartItem> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        order.setCreatedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);

        int count = 0;
        for (CartItem ci : new ArrayList<>(items)) {
            OrderLine line = new OrderLine();
            line.setBook(ci.getBook());
            line.setQuantity(ci.getQuantity());
            line.setSubtotal(ci.getBook().getPrice());
            line.setOrder(savedOrder);
            orderLineRepository.save(line);
            count++;
        }

        // clear cart
        for (CartItem ci : new ArrayList<>(items)) {
            cart.removeItem(ci);
            cartItemRepository.delete(ci);
        }
        cartRepository.save(cart);

        return count;
    }

    //Simple process payment method, to be changed later, just updates the stock of the book and clears the session to empty the cart.
    public boolean processPayment(Map<Long, Integer> cart, String cardNumber, String expiry, String cvv) {
        if(checkCard(cardNumber,expiry,cvv)) {
            System.out.println("not expired");
            cart.forEach((bookId, qty) -> {
                bookRepository.findById(bookId).ifPresent(book -> {
                    int stock = Math.max(0, book.getStock() - qty);
                    book.setStock(stock);
                    bookRepository.save(book);
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