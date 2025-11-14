package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final OrderService orderService;

    /**
     * Constructor.
     *
     * @param cartRepository Live Repo of Carts.
     * @param cartItemRepository Live Repo of CartItems.
     * @param bookRepository Live Repo of Books.
     * @param orderService Service for creating Orders.
     */
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       OrderService orderService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.orderService = orderService;
    }

    /**
     * Add item to the cart.
     * @param cart The cart to add item to.
     * @param bookId The ID of the book to add.
     * @param quantity The quantity of the book to add.
     */
    public void addItem(Cart cart, Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem(cart, book, quantity);
            cart.addItem(item);
            cartItemRepository.save(item);
        }

        cartRepository.save(cart);
    }

    /**
     * Remove item from the cart.
     *
     * @param cart The cart to remove item from.
     * @param bookId The ID of the book to remove.
     * @param quantity The quantity of the book to remove.
     */
    public void removeItem(Cart cart, Long bookId, int quantity) {
        cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst()
                .ifPresent(item -> {
                    int remaining = item.getQuantity() - quantity;
                    if (remaining > 0) {
                        item.setQuantity(remaining);
                    } else {
                        cart.removeItem(item);
                        cartItemRepository.delete(item);
                    }
                });

        cartRepository.save(cart);
    }

    /**
     * Calculate the total price of cart items.
     *
     * @param cart The cart to calculate the total price of.
     * @return The total price of the cart items.
     */
    public double total(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(i -> i.getBook().getPrice() * i.getQuantity())
                .sum();
    }

    /**
     * Detailed mapping: Book -> quantity
     *
     * @param cart The cart to map.
     * @return Hashmap of Books and associated quantities.
     */
    public Map<Book, Integer> detailed(Cart cart) {
        Map<Book, Integer> result = new LinkedHashMap<>();
        cart.getItems().forEach(i -> result.put(i.getBook(), i.getQuantity()));
        return result;
    }

    /**
     * Checkout: create Order with OrderLines, update stock, clear cart
     *
     * @param cartId Cart ID to attach the order line to.
     * @return The newly created Order.
     */
    public Order checkout(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        if (cart.getItems().isEmpty()) throw new IllegalStateException("Cart is empty");

        // Create Order
        Order order = new Order();
        order.setCustomer(cart.getCustomer());

        Order finalOrder = order;
        cart.getItems().forEach(item -> {
            // Update stock
            Book book = item.getBook();
            book.setStock(Math.max(0, book.getStock() - item.getQuantity()));
            bookRepository.save(book);

            // Create OrderLine
            OrderLine orderLine = new OrderLine();
            orderLine.setBook(book);
            orderLine.setQuantity(item.getQuantity());
            orderLine.setPrice(BigDecimal.valueOf(book.getPrice()));
            orderLine.setOrder(finalOrder);
            finalOrder.addOrderLine(orderLine);
        });

        order = orderService.create(order); // persist Order

        // Clear cart
        cart.getItems().forEach(item -> {
            cartItemRepository.delete(item);
            item.setCart(null);
        });
        cart.getItems().clear();
        cart.deactivate();
        cartRepository.save(cart);

        return order;
    }
}