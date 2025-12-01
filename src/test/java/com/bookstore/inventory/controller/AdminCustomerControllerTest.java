package com.bookstore.inventory.controller;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AdminCustomerController}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Web/controller layer.
 * <h2>Scope:</h2> Admin customer listing, search, CRUD form flow, password rules.
 * <h2>Dependencies:</h2> {@link CustomerRepository} (mocked).
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminCustomerController Unit Tests")
class AdminCustomerControllerTest {

    @Mock
    private CustomerRepository repo;

    @InjectMocks
    private AdminCustomerController controller;

    @Test
    @DisplayName("list without keyword returns all customers")
    void list_noKeyword_returnsAll() {
        Model model = new ExtendedModelMap();
        when(repo.findAll()).thenReturn(List.of(new Customer()));

        String view = controller.list(null, model);

        assertEquals("admin/customers", view);
        verify(repo).findAll();
        assertNotNull(model.getAttribute("customers"));
    }

    @Test
    @DisplayName("list with keyword delegates to search")
    void list_withKeyword_usesSearch() {
        Model model = new ExtendedModelMap();
        when(repo.search("john")).thenReturn(List.of(new Customer()));

        String view = controller.list("John", model);

        assertEquals("admin/customers", view);
        verify(repo).search("john");
    }

    @Test
    @DisplayName("newCustomer adds empty customer to model")
    void newCustomer_populatesModel() {
        Model model = new ExtendedModelMap();

        String view = controller.newCustomer(model);

        assertEquals("admin/customer-form", view);
        assertInstanceOf(Customer.class, model.getAttribute("customer"));
    }

    @Test
    @DisplayName("editCustomer loads existing customer")
    void editCustomer_populatesModel() {
        Customer c = new Customer();
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        Model model = new ExtendedModelMap();

        String view = controller.editCustomer(1L, model);

        assertEquals("admin/customer-form", view);
        assertSame(c, model.getAttribute("customer"));
    }

    @Test
    @DisplayName("saveCustomer encodes password for new customer")
    void saveCustomer_new_encodesPassword() {
        Customer c = new Customer();
        c.setPassword("plain");

        controller.saveCustomer(c);

        assertNotEquals("plain", c.getPassword());
        verify(repo).save(c);
    }

    @Test
    @DisplayName("saveCustomer keeps existing password when editing")
    void saveCustomer_edit_keepsPassword() {
        Customer existing = new Customer();
        existing.setId(1L);
        existing.setPassword("encoded");
        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        Customer form = new Customer();
        form.setId(1L);
        form.setPassword("ignored");

        controller.saveCustomer(form);

        assertEquals("encoded", form.getPassword());
        verify(repo).save(form);
    }

    @Test
    @DisplayName("delete removes customer and redirects")
    void delete_redirects() {
        String view = controller.delete(1L);

        assertEquals("redirect:/admin/customers", view);
        verify(repo).deleteById(1L);
    }
}