package com.bookstore.common.repository;

import com.bookstore.common.model.Address;
import com.bookstore.common.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link AddressRepository}.
 * <p>
 * Test Category: Integration Tests (IT) – persistence layer.
 * Scope: saving and loading {@link Address} entities and their association to {@link Customer}.
 * Dependencies: Spring Data JPA, H2 in-memory database, {@link CustomerRepository}.
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DataJpaTest
@DisplayName("AddressRepository JPA Integration Tests")
class AddressRepositoryIT {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Test: Save an address linked to a customer and load it back by id.
     */
    @Test
    @DisplayName("Should save and find address by id with customer relation")
    void testSaveAndFindById() {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        Customer savedCustomer = customerRepository.save(customer);

        Address address = new Address(
                null,
                "John",
                "Doe",
                "123 Main St",
                "Unit 4",
                "Springfield",
                "IL",
                "12345",
                "USA"
        );
        address.setCustomer(savedCustomer);

        Address saved = addressRepository.save(address);
        assertNotNull(saved.getId());

        Address found = addressRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("John", found.getFirstName());
        assertEquals("Doe", found.getLastName());
        assertNotNull(found.getCustomer());
        assertEquals(savedCustomer.getId(), found.getCustomer().getId());
    }
}