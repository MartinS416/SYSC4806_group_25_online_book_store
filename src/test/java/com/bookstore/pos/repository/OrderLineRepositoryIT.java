package com.bookstore.pos.repository;

import com.bookstore.demo.model.Address;
import com.bookstore.demo.model.Book;
import com.bookstore.demo.model.Customer;
import com.bookstore.demo.repository.AddressRepository;
import com.bookstore.demo.repository.BookRepository;
import com.bookstore.demo.repository.CustomerRepository;
import com.bookstore.pos.model.Order;
import com.bookstore.pos.model.OrderLine;
import com.bookstore.pos.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link OrderLineRepository}.
 * <p>
 * Level: integration.
 * Verifies finder methods and reporting queries used for top-selling
 * books and revenue by category.
 */
@DataJpaTest
class OrderLineRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLineRepository orderLineRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookRepository books;

    /**
     * Verifies that order lines can be found by order id and by order entity.
     */
    @Test
    void findByOrderAndOrderId_returnMatchingLines() {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setTotalAmount(BigDecimal.ZERO);

        Customer customer = new Customer();
        customerRepository.save(customer);

        Address address = new Address();
        address.setCustomer(customer);
        address.setFirstName("john");
        address.setLastName("johnson");
        address.setCity("ottawa");
        address.setStreet("123 street");
        address.setRegion("ont");
        address.setPostcode("A1A 1A1");

        addressRepository.save(address);

        Book book = new Book();
        books.save(book);

        order.setAddress(address);
        order.setCustomer(customer);

        order = orderRepository.save(order);

        OrderLine line = new OrderLine();
        line.setOrder(order);
        line.setPrice(new BigDecimal("10.00"));
        line.setQuantity(1);
        line.setBook(book);

        orderLineRepository.save(line);

        List<OrderLine> byOrder = orderLineRepository.findByOrder(order);
        List<OrderLine> byOrderId = orderLineRepository.findByOrderId(order.getId());

        assertEquals(1, byOrder.size());
        assertEquals(1, byOrderId.size());
    }

    /**
     * Verifies that {@link OrderLineRepository#getTopSellingBooks()} returns
     * aggregated rows when order lines exist.
     */
    @Test
    void getTopSellingBooks_returnsAggregatedRows() {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setTotalAmount(BigDecimal.ZERO);

        Customer customer = new Customer();
        customerRepository.save(customer);

        Address address = new Address();
        address.setCustomer(customer);
        address.setFirstName("john");
        address.setLastName("johnson");
        address.setCity("ottawa");
        address.setStreet("123 street");
        address.setRegion("ont");
        address.setPostcode("A1A 1A1");

        addressRepository.save(address);

        order.setAddress(address);
        order.setCustomer(customer);

        order = orderRepository.save(order);

        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        book.setTitle("Test Book");

        OrderLine line = new OrderLine();
        line.setOrder(order);
        line.setBook(book);
        line.setQuantity(2);
        line.setPrice(new BigDecimal("10.00"));
        orderLineRepository.save(line);

        List<?> rows = orderLineRepository.getTopSellingBooks();
        assertFalse(rows.isEmpty());
    }

    /**
     * Verifies that {@link OrderLineRepository#getRevenueByCategory()} returns
     * aggregated rows when order lines with categories exist.
     */
    @Test
    void getRevenueByCategory_returnsAggregatedRows() {
        Order order = new Order();

        Customer customer = new Customer();
        customerRepository.save(customer);

        Address address = new Address();
        address.setCustomer(customer);
        address.setFirstName("john");
        address.setLastName("johnson");
        address.setCity("ottawa");
        address.setStreet("123 street");
        address.setRegion("ont");
        address.setPostcode("A1A 1A1");

        addressRepository.save(address);

        order.setAddress(address);
        order.setCustomer(customer);

        order = orderRepository.save(order);

        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 2L);
        book.setCategory("Programming");

        OrderLine line = new OrderLine();
        line.setOrder(order);
        line.setBook(book);
        line.setQuantity(1);
        line.setPrice(new BigDecimal("30.00"));
        orderLineRepository.save(line);

        List<?> rows = orderLineRepository.getRevenueByCategory();
        assertFalse(rows.isEmpty());
    }
}