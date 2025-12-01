package com.bookstore.common.service;

import com.bookstore.common.model.Address;
import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import com.bookstore.pos.model.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CustomerService}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Service layer.
 * <h2>Scope:</h2>
 * <ul>
 *   <li>CRUD operations for {@link Customer} via {@link CustomerRepository}.</li>
 *   <li>Relationship helpers for addresses and carts.</li>
 * </ul>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("create delegates to repository.save")
    void create_savesCustomer() {
        Customer c = new Customer();
        when(customerRepository.save(c)).thenReturn(c);

        Customer saved = customerService.create(c);

        assertSame(c, saved);
        verify(customerRepository).save(c);
    }

    @Test
    @DisplayName("findById returns existing customer")
    void findById_existing() {
        Customer c = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        Customer found = customerService.findById(1L);

        assertSame(c, found);
    }

    @Test
    @DisplayName("findById throws when missing")
    void findById_missing() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> customerService.findById(1L));
    }

    @Test
    @DisplayName("findAll delegates to repository.findAll")
    void findAll_delegates() {
        when(customerRepository.findAll()).thenReturn(List.of(new Customer()));

        List<?> all = customerService.findAll();

        assertEquals(1, all.size());
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("update uses CustomerController.getCustomer helper and saves")
    void update_updatesCustomer() {
        Customer existing = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(existing);

        Customer input = new Customer();
        input.setUsername("u");
        input.setPassword("p");
        input.setEmail("e");

        Customer result = customerService.update(1L, input);

        assertSame(existing, result);
        verify(customerRepository).save(existing);
        assertEquals("u", existing.getUsername());
        assertEquals("p", existing.getPassword());
        assertEquals("e", existing.getEmail());
    }

    @Test
    @DisplayName("delete delegates to repository.deleteById")
    void delete_delegates() {
        customerService.delete(5L);

        verify(customerRepository).deleteById(5L);
    }

    @Test
    @DisplayName("addAddress sets bidirectional relation and saves customer")
    void addAddress_setsRelationAndSaves() {
        Customer c = new Customer();
        Address a = new Address();

        customerService.addAddress(c, a);

        assertTrue(c.getAddresses().contains(a));
        assertSame(c, a.getCustomer());
        verify(customerRepository).save(c);
    }

    @Test
    @DisplayName("addCart sets bidirectional relation and saves customer")
    void addCart_setsRelationAndSaves() {
        Customer c = new Customer();
        Cart cart = new Cart();

        customerService.addCart(c, cart);

        assertTrue(c.getCarts().contains(cart));
        assertSame(c, cart.getCustomer());
        verify(customerRepository).save(c);
    }
}