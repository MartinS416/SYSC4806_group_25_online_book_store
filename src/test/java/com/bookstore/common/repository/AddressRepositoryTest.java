package com.bookstore.common.repository;

import com.bookstore.common.model.Address;
import com.bookstore.common.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testSaveAndFindById() {

        // Save customer first
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        Customer savedCustomer = customerRepository.save(customer);

        // Create address linked to saved customer
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
    }
}