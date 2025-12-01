package com.bookstore.common.service;

import com.bookstore.common.model.Address;
import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.AddressRepository;

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

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    void testSaveAddress() {
        Address address = new Address();
        address.setFirstName("John");
        when(addressRepository.save(address)).thenReturn(address);

        Address result = addressService.save(address);

        assertEquals("John", result.getFirstName());
        verify(addressRepository).save(address);
    }

    @Test
    void testFindById() {
        Address address = new Address();
        address.setFirstName("Jane");
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        Address result = addressService.findById(1L);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
    }

    @Test
    void testFindAll() {
        Address a1 = new Address();
        Address a2 = new Address();
        when(addressRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        List<Address> result = addressService.findAll();

        assertEquals(2, result.size());
        verify(addressRepository).findAll();
    }

    @Test
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
    void testDelete() {
        addressService.delete(5L);
        verify(addressRepository).deleteById(5L);
    }
}