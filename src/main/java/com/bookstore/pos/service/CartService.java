package com.bookstore.pos.service;

import com.bookstore.demo.model.*;
import com.bookstore.demo.repository.*;
import com.bookstore.pos.model.Cart;
import com.bookstore.pos.model.CartItem;
import com.bookstore.pos.model.Order;
import com.bookstore.pos.model.OrderLine;
import com.bookstore.pos.repository.CartItemRepository;
import com.bookstore.pos.repository.CartRepository;
import com.bookstore.pos.repository.OrderLineRepository;
import com.bookstore.pos.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service for managing persistent shopping carts and checkout operations.
 * <p>
 * This service is responsible for adding and removing cart items, computing totals,
 * creating orders from carts, and clearing cart contents. It does not perform
 * payment processing.
 */
@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final OrderLineRepository orderLineRepository;
    private final CustomerRepository customerRepository;

    private List<Long> killList;

    /**
     * Creates a new CartService with the required repositories.
     *
     * @param cartRepository       repository for {@link Cart} entities
     * @param cartItemRepository   repository for {@link CartItem} entities
     * @param bookRepository       repository for {@link Book} entities
     * @param orderRepository      repository for {@link Order} entities
     * @param orderLineRepository  repository for {@link OrderLine} entities
     * @param customerRepository   repository for {@link Customer} entities
     * @param addressRepository    repository for {@link Address} entities
     */
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       OrderRepository orderRepository,
                       OrderLineRepository orderLineRepository,
                       CustomerRepository customerRepository,
                       AddressRepository addressRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.killList = new ArrayList<>();
    }

    /**
     * Finds the active cart for a customer or creates a new active cart if none exists.
     *
     * @param customerId the identifier of the customer
     * @return the existing active cart or a newly created cart
     * @throws NoSuchElementException if the customer cannot be found
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
     * Adds a quantity of a book to the specified cart, creating or updating a {@link CartItem}.
     *
     * @param cartId   the identifier of the cart to modify
     * @param bookId   the identifier of the book to add
     * @param quantity the number of units to add (must be non-negative)
     * @return the created or updated CartItem
     * @throws IllegalArgumentException if quantity is negative
     * @throws NoSuchElementException   if the cart or book cannot be found
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

        killList.remove(cartId);

        CartItem saved = cartItemRepository.save(toSave);
        cartRepository.save(cart);
        return saved;
    }

    /**
     * Removes a quantity of a book from the specified cart.
     * <p>
     * If the resulting quantity is zero or less, the cart item is deleted. Book stock is
     *  increased by the removed quantity.
     *
     * @param cartId   the identifier of the cart to modify
     * @param bookId   the identifier of the book to remove
     * @param quantity the number of units to remove (must be non-negative)
     * @throws IllegalArgumentException if quantity is negative
     * @throws NoSuchElementException   if the cart or cart item cannot be found
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

        Book book = bookRepository.getById(bookId);
        book.setStock(book.getStock() + quantity);

        if (quantity >= existing.getQuantity()) {
            cart.removeItem(existing);
            cartItemRepository.delete(existing);
        } else {
            existing.setQuantity(existing.getQuantity() - quantity);
            cartItemRepository.save(existing);
        }

        killList.remove(cartId);

        cartRepository.save(cart);
    }

    /**
     * Returns a map of books and their quantities for the specified cart.
     *
     * @param cartId the identifier of the cart
     * @return a {@link LinkedHashMap} preserving insertion order of books to quantities
     * @throws NoSuchElementException if the cart cannot be found
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
     * Calculates the total price of all items in the specified cart.
     *
     * @param cartId the identifier of the cart
     * @return the total price as {@link BigDecimal}
     * @throws NoSuchElementException if the cart cannot be found
     */
    @Transactional
    public BigDecimal calculateTotal(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found: " + cartId));
        return BigDecimal.valueOf(
                cart.getItems().stream()
                        .mapToDouble(ci -> ci.getBook().getPrice().doubleValue() * ci.getQuantity())
                        .sum()
        );
    }

    /**
     * Clears all items from the specified cart while keeping the cart entity itself.
     * <p>
     * This method removes all {@link CartItem} entries associated with the cart.
     *
     * @param cartId the identifier of the cart to clear
     * @throws NoSuchElementException if the cart cannot be found
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

        System.out.println("Cart " + cartId + " cleared successfully. Items removed: " + copy.size());
    }

    /**
     * Creates an {@link Order} and associated {@link OrderLine}s from the specified cart.
     * <p>
     * This method does not clear the cart; callers are responsible for invoking
     * {@link #clearCart(Long)} after a successful checkout.
     *
     * @param cartId  the identifier of the cart to check out
     * @param address the billing or shipping address to associate with the order
     * @return the number of order lines created
     * @throws NoSuchElementException if the cart cannot be found
     * @throws IllegalStateException  if the cart is empty
     */
    @Transactional
    public int checkout(Long cartId, Address address) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found: " + cartId));

        List<CartItem> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        addressRepository.save(address);

        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        order.setAddress(address);
        order.setCreatedAt(Instant.now());

        order.setName(address.getFirstName() + " " + address.getLastName());
        order.setEmail(cart.getCustomer().getEmail());
        order.setPhone(cart.getCustomer().getPhone());

         Order savedOrder = orderRepository.save(order);

        int count = 0;

        for (CartItem ci : items) {

            OrderLine line = new OrderLine();
            line.setOrder(savedOrder); // Use the saved order
            line.setBook(ci.getBook());
            line.setQuantity(ci.getQuantity());
            line.setPrice(ci.getBook().getPrice());
            line.setSubtotal(ci.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(ci.getQuantity())));

            savedOrder.addOrderLine(line);

            orderLineRepository.save(line);

            count++;
        }

        return count;
    }


    /**
     * Periodically clears carts that have been idle for at least two scheduled runs.
     * <p>
     * The method runs every five minutes and uses an in-memory {@code killList}
     * to determine which carts should be cleared.
     */
    @Scheduled(fixedRate = 300000)
    public void emptyOldCarts() {
        for (Cart c : cartRepository.findAll()) {
            if (killList.contains(c.getId())) {
                clearCart(c.getId());
                killList.remove(c.getId());
            } else {
                killList.add(c.getId());
            }
        }
    }

    /**
     * Performs a basic card expiry check using the {@code MM/yy} format.
     * <p>
     * The current implementation only validates that the expiry month and year
     * are not in the past. It does not perform Luhn checks or contact a payment gateway.
     *
     * @param cardNumber the card number (currently unused)
     * @param expiry     the expiry date in {@code MM/yy} format
     * @param cvv        the card CVV (currently unused)
     * @return {@code true} if the card is not expired; {@code false} otherwise
     */
    public boolean checkCard(String cardNumber, String expiry, String cvv) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            String currentDate = LocalDate.now().format(formatter);
            String[] current = currentDate.split("/");
            String[] exp = expiry.split("/");

            int currentYear = Integer.parseInt(current[1]);
            int expiryYear = Integer.parseInt(exp[1]);

            if (currentYear != expiryYear) {
                return currentYear < expiryYear;
            }

            int currentMonth = Integer.parseInt(current[0]);
            int expiryMonth = Integer.parseInt(exp[0]);
            return currentMonth <= expiryMonth;
        } catch (Exception e) {
            System.err.println("Card validation error: " + e.getMessage());
            return false;
        }
    }
}