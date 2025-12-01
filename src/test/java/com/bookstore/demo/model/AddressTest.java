package com.bookstore.demo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

class AddressTest {

    @Test
    void testConstructorAndGetters() {
        Address address = new Address(
                1L,
                "John",
                "Doe",
                "123 Main St",
                "Unit 4",
                "Springfield",
                "IL",
                "12345",
                "USA"
        );

        assertEquals(1L, address.getId());
        assertEquals("John", address.getFirstName());
        assertEquals("Doe", address.getLastName());
        assertEquals("123 Main St", address.getStreet());
        assertEquals("Unit 4", address.getUnit());
        assertEquals("Springfield", address.getCity());
        assertEquals("IL", address.getRegion());
        assertEquals("12345", address.getPostcode());
        assertEquals("USA", address.getCountry());
    }

    @Test
    void testSetters() {
        Address address = new Address();
        address.setFirstName("Jane");
        address.setLastName("Smith");
        address.setStreet("456 Oak Ave");
        address.setUnit("Suite 9");
        address.setCity("Metropolis");
        address.setRegion("NY");
        address.setPostcode("67890");
        address.setCountry("USA");

        assertEquals("Jane", address.getFirstName());
        assertEquals("Smith", address.getLastName());
        assertEquals("456 Oak Ave", address.getStreet());
        assertEquals("Suite 9", address.getUnit());
        assertEquals("Metropolis", address.getCity());
        assertEquals("NY", address.getRegion());
        assertEquals("67890", address.getPostcode());
        assertEquals("USA", address.getCountry());
    }

    @Test
    void testCustomerRelationship() {
        Address address = new Address();
        Customer mockCustomer = Mockito.mock(Customer.class);

        address.setCustomer(mockCustomer);

        assertSame(mockCustomer, address.getCustomer());
    }
}
