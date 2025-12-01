package com.bookstore.pos.service;

import com.bookstore.common.model.Address;
import com.bookstore.common.model.Customer;
import com.bookstore.inventory.model.Book;
import com.bookstore.pos.model.CartItem;
import com.bookstore.pos.model.Order;
import com.bookstore.pos.model.OrderStatus;
import com.bookstore.pos.model.Payment;
import com.bookstore.pos.repository.OrderLineRepository;
import com.bookstore.pos.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrderService} in the POS domain.
 * <p>
 * These tests verify the orchestration logic performed by the service,
 * including creation of orders from cart items, updating status and payment,
 * and mapping reporting query results into DTO-style structures.
 * <p>
 * The tests are pure unit tests that use Mockito to isolate the
 * {@link OrderRepository} and {@link OrderLineRepository} dependencies.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLineRepository orderLineRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Address address;

    /**
     * Initializes a sample {@link Customer} and {@link Address} used across
     * multiple test cases.
     */
    @BeforeEach
    void setUp() {
        customer = new Customer();
        ReflectionTestUtils.setField(customer, "id", 1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("1234567890");

        address = new Address();
        address.setCity("Ottawa");
        address.setRegion("ON");
        address.setCountry("CA");
        address.setStreet("123 Street");
    }

    /**
     * Verifies that {@link OrderService#getRecentOrders()} delegates to
     * {@link OrderRepository#findAllByOrderByCreatedAtDesc()}.
     */
    @Test
    void getRecentOrders_delegatesToRepository() {
        orderService.getRecentOrders();
        verify(orderRepository).findAllByOrderByCreatedAtDesc();
    }

    /**
     * Verifies that {@link OrderService#create(Order)} calls
     * {@link OrderRepository#save(Object)} and returns the saved instance.
     */
    @Test
    void create_savesOrder() {
        Order order = new Order();
        when(orderRepository.save(order)).thenReturn(order);

        Order saved = orderService.create(order);

        assertSame(order, saved);
        verify(orderRepository).save(order);
    }

    /**
     * Verifies that {@link OrderService#findById(Long)} returns the existing
     * order when the repository finds a matching entity.
     */
    @Test
    void findById_existingOrder_returnsOrder() {
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order));

        Order found = orderService.findById(1L);

        assertSame(order, found);
    }

    /**
     * Verifies that {@link OrderService#findById(Long)} throws an
     * {@link IllegalArgumentException} when the repository does not find an order.
     */
    @Test
    void findById_missingOrder_throws() {
        when(orderRepository.findById(99L)).thenReturn(java.util.Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> orderService.findById(99L));
    }

    /**
     * Verifies that {@link OrderService#updateStatus(Long, OrderStatus)} loads
     * the order, updates its status, and saves the updated order.
     */
    @Test
    void updateStatus_changesStatusAndPersists() {
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order updated = orderService.updateStatus(1L, OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, updated.getStatus());
        verify(orderRepository).save(order);
    }

    /**
     * Verifies that {@link OrderService#addPayment(Long, Payment)} associates
     * the given payment with the order and persists the change.
     */
    @Test
    void addPayment_setsPaymentAndSaves() {
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Payment payment = new Payment();
        Order result = orderService.addPayment(1L, payment);

        assertSame(payment, result.getPayment());
        verify(orderRepository).save(order);
    }

    /**
     * Verifies that {@link OrderService#delete(Long)} forwards the identifier
     * to {@link OrderRepository#deleteById(Object)}.
     */
    @Test
    void delete_delegatesToRepository() {
        orderService.delete(5L);
        verify(orderRepository).deleteById(5L);
    }

    /**
     * Verifies that {@link OrderService#createOrder(Customer, Address, java.util.List)}
     * builds order lines from cart items, sets customer details, and accumulates
     * the total amount correctly.
     */
    @Test
    void createOrder_buildsLinesAndTotals() {
        CartItem item1 = new CartItem();
        item1.setQuantity(2);
        item1.setBook(new Book());
        item1.getBook().setPrice(new BigDecimal("10.00"));

        CartItem item2 = new CartItem();
        item2.setQuantity(1);
        item2.setBook(new Book());
        item2.getBook().setPrice(new BigDecimal("5.50"));

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order order = orderService.createOrder(customer, address, List.of(item1, item2));

        assertEquals("John Doe", order.getName());
        assertEquals(customer.getEmail(), order.getEmail());
        assertEquals(customer.getPhone(), order.getPhone());
        assertEquals(2, order.getOrderLines().size());
        assertEquals(new BigDecimal("25.50"), order.getTotalAmount());
    }

    /**
     * Verifies that {@link OrderService#getDailyRevenue()} converts the raw
     * query rows returned by the repository into a list of maps containing
     * {@code date} and {@code revenue} keys.
     */
    @Test
    void getDailyRevenue_mapsRowsToListOfMaps() {
        Object[] row = new Object[]{Instant.now(), new BigDecimal("100.00")};
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);

        when(orderRepository.getDailyRevenue()).thenReturn(rows);

        List<Map<String, Object>> result = orderService.getDailyRevenue();

        assertEquals(1, result.size());
        assertTrue(result.getFirst().containsKey("date"));
        assertEquals(new BigDecimal("100.00"), result.getFirst().get("revenue"));
    }

    /**
     * Verifies that {@link OrderService#getTopSellingBooks()} maps raw rows
     * from {@link OrderLineRepository#getTopSellingBooks()} into maps with
     * {@code title} and {@code units} keys.
     */
    @Test
    void getTopSellingBooks_mapsRowsToListOfMaps() {
        Object[] row = new Object[]{"Book A", 5L};
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);

        when(orderLineRepository.getTopSellingBooks()).thenReturn(rows);

        List<Map<String, Object>> result = orderService.getTopSellingBooks();

        assertEquals(1, result.size());
        assertEquals("Book A", result.getFirst().get("title"));
        assertEquals(5L, result.getFirst().get("units"));
    }


    /**
     * Verifies that {@link OrderService#getRevenueByCategory()} maps raw rows
     * from {@link OrderLineRepository#getRevenueByCategory()} into maps with
     * {@code category} and {@code revenue} keys.
     */
    @Test
    void getRevenueByCategory_mapsRowsToListOfMaps() {
        Object[] row = new Object[]{"Programming", new BigDecimal("200.00")};
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);

        when(orderLineRepository.getRevenueByCategory()).thenReturn(rows);

        List<Map<String, Object>> result = orderService.getRevenueByCategory();

        assertEquals(1, result.size());
        assertEquals("Programming", result.getFirst().get("category"));
        assertEquals(new BigDecimal("200.00"), result.getFirst().get("revenue"));
    }
}