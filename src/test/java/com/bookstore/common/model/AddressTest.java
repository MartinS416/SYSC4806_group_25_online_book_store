package com.bookstore.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Address}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Domain model.
 * <h2>Scope:</h2>
 * <ul>
 *   <li>Constructor and getter behavior for address fields.</li>
 *   <li>Setter behavior for updating address properties.</li>
 *   <li>Association between {@link Address} and {@link Customer}.</li>
 * </ul>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-11-02
 */
class AddressTest {

    /**
     * Test: all-args constructor populates address fields correctly.
     */
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

    /**
     * Test: setters update address fields as expected.
     */
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

    /**
     * Test: customer relationship can be set and retrieved.
     */
    @Test
    void testCustomerRelationship() {
        Address address = new Address();
        Customer mockCustomer = Mockito.mock(Customer.class);

        address.setCustomer(mockCustomer);

        assertSame(mockCustomer, address.getCustomer());
    }
}