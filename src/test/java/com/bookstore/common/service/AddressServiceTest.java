package com.bookstore.common.service;

import com.bookstore.common.model.Address;
import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.AddressRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AddressService}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Service layer.
 * <h2>Scope:</h2>
 * <ul>
 *   <li>CRUD operations for {@link Address} via {@link AddressRepository}.</li>
 *   <li>Update logic including customer association fallback behavior.</li>
 * </ul>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025.11.11
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Unit Tests")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    @DisplayName("save delegates to repository.save")
    void testSaveAddress() {
        Address address = new Address();
        address.setFirstName("John");
        when(addressRepository.save(address)).thenReturn(address);

        Address result = addressService.save(address);

        assertEquals("John", result.getFirstName());
        verify(addressRepository).save(address);
    }

    @Test
    @DisplayName("findById returns existing address")
    void testFindById() {
        Address address = new Address();
        address.setFirstName("Jane");
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        Address result = addressService.findById(1L);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
    }

    @Test
    @DisplayName("findAll delegates to repository.findAll")
    void testFindAll() {
        Address a1 = new Address();
        Address a2 = new Address();
        when(addressRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        List<?> result = addressService.findAll();

        assertEquals(2, result.size());
        verify(addressRepository).findAll();
    }

    @Test
    @DisplayName("update copies fields and sets customer from updated")
    void testUpdate() {
        Address existing = new Address();
        existing.setFirstName("Old");

        Address updated = new Address();
        updated.setFirstName("New");
        updated.setCustomer(new Customer());

        when(addressRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(addressRepository.save(existing)).thenReturn(existing);

        Address result = addressService.update(1L, updated);

        assertEquals("New", result.getFirstName());
        assertSame(updated.getCustomer(), result.getCustomer());
    }

    @Test
    @DisplayName("delete delegates to repository.deleteById")
    void testDelete() {
        addressService.delete(5L);

        verify(addressRepository).deleteById(5L);
    }
}