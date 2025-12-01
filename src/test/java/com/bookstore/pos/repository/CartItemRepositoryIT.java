package com.bookstore.pos.repository;

import com.bookstore.demo.model.Book;
import com.bookstore.demo.model.Customer;
import com.bookstore.demo.repository.BookRepository;
import com.bookstore.demo.repository.CustomerRepository;
import com.bookstore.pos.model.Cart;
import com.bookstore.pos.model.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link CartItemRepository}.
 * <p>
 * Level: integration (Spring Data JPA).
 * Verifies finder methods used by cart operations.
 */
@DataJpaTest
class CartItemRepositoryIT {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookRepository books;

    /**
     * Verifies that {@link CartItemRepository#findByCartId(Long)} returns
     * all items for the specified cart id.
     */
    @Test
    void findByCartId_returnsItemsForCart() {
        Customer customer = new Customer();
        customerRepository.save(customer);
        Cart cart = new Cart(customer);
        cart = cartRepository.save(cart);

        Book book = new Book();
        book.setPrice(BigDecimal.TEN);
        books.save(book);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setBook(book);
        item.setQuantity(2);

        cartItemRepository.save(item);

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        assertEquals(1, items.size());
        assertEquals(cart.getId(), items.get(0).getCart().getId());
    }

    /**
     * Verifies that {@link CartItemRepository#findByCartIdAndBookId(Long, Long)}
     * returns the expected item when it exists.
     */
    @Test
    void findByCartIdAndBookId_returnsMatchingItem() {
        Customer customer = new Customer();
        customerRepository.save(customer);
        Cart cart = new Cart(customer);
        cart = cartRepository.save(cart);

        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 5L);
        book.setPrice(BigDecimal.ONE);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setBook(book);
        item.setQuantity(1);

        cartItemRepository.save(item);

        Optional<CartItem> found = cartItemRepository.findByCartIdAndBookId(cart.getId(), 5L);

        assertTrue(found.isPresent());
        assertEquals(5L, found.get().getBook().getId());
    }

    /**
     * Verifies that {@link CartItemRepository#deleteByCartId(Long)} removes
     * all items for the specified cart id.
     */
    @Test
    void deleteByCartId_removesAllItemsForCart() {
        Customer customer = new Customer();
        customerRepository.save(customer);
        Cart cart = new Cart(customer);
        cart = cartRepository.save(cart);

        Book book = new Book();
        books.save(book);

        CartItem item = new CartItem();
        item.setBook(book);
        item.setCart(cart);
        cartItemRepository.save(item);

        cartItemRepository.deleteByCartId(cart.getId());

        List<CartItem> after = cartItemRepository.findByCartId(cart.getId());
        assertTrue(after.isEmpty());
    }
}