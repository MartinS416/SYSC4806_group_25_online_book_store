package com.bookstore.common.controller;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CustomerController}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – REST controller layer.
 * <h2>Scope:</h2> CRUD operations for /api/customers, including 404 handling.
 * <h2>Dependencies:</h2> {@link CustomerRepository} (mocked).
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerController Unit Tests")
class CustomerControllerTest {

    @Mock
    private CustomerRepository customers;

    @InjectMocks
    private CustomerController controller;

    @Test
    @DisplayName("all returns list from repository")
    void all_returnsAll() {
        when(customers.findAll()).thenReturn(List.of(new Customer()));

        List<Customer> result = controller.all();

        assertEquals(1, result.size());
        verify(customers).findAll();
    }

    @Test
    @DisplayName("get returns existing customer")
    void get_existing_returnsCustomer() {
        Customer c = new Customer();
        when(customers.findById(1L)).thenReturn(Optional.of(c));

        Customer result = controller.get(1L);

        assertSame(c, result);
    }

    @Test
    @DisplayName("get throws 404 when missing")
    void get_missing_throwsNotFound() {
        when(customers.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> controller.get(1L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("create clears id, saves and returns 201")
    void create_savesAndReturnsCreated() {
        Customer input = new Customer();
        input.setId(99L);
        Customer saved = new Customer();
        saved.setId(1L);
        when(customers.save(any(Customer.class))).thenReturn(saved);

        ResponseEntity<?> response = controller.create(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(saved, response.getBody());
        assertNull(input.getId());
        verify(customers).save(input);
    }

    @Test
    @DisplayName("update copies fields and saves existing")
    void update_updatesExisting() {
        Customer existing = new Customer();
        existing.setId(1L);
        when(customers.findById(1L)).thenReturn(Optional.of(existing));
        when(customers.save(existing)).thenReturn(existing); // important

        Customer input = new Customer();
        input.setUsername("u");
        input.setPassword("p");
        input.setEmail("e");
        input.setFirstName("f");
        input.setLastName("l");
        input.setPhone("ph");

        Customer result = controller.update(1L, input);

        assertSame(existing, result);
        verify(customers).save(existing);
        assertEquals("u", existing.getUsername());
        assertEquals("p", existing.getPassword());
        assertEquals("e", existing.getEmail());
        assertEquals("f", existing.getFirstName());
        assertEquals("l", existing.getLastName());
        assertEquals("ph", existing.getPhone());
    }

    @Test
    @DisplayName("update throws 404 when customer missing")
    void update_missing_throwsNotFound() {
        when(customers.findById(1L)).thenReturn(Optional.empty());
        Customer input = new Customer();

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> controller.update(1L, input));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("delete removes existing and returns 204")
    void delete_existing_deletesAndReturnsNoContent() {
        when(customers.existsById(1L)).thenReturn(true);

        ResponseEntity<?> response = controller.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customers).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws 404 when customer missing")
    void delete_missing_throwsNotFound() {
        when(customers.existsById(1L)).thenReturn(false);

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> controller.delete(1L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(customers, never()).deleteById(anyLong());
    }
}