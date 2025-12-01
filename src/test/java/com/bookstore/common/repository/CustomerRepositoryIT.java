package com.bookstore.common.repository;

import com.bookstore.common.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CustomerRepository}.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Category:</strong> Integration Tests (IT)</li>
 *   <li><strong>Layer:</strong> Persistence / JPA repository</li>
 *   <li><strong>Scope:</strong> Customer lookup by email and keyword search</li>
 *   <li><strong>Dependencies:</strong> Spring Data JPA, H2 in-memory database</li>
 * </ul>
 *
 * <h2>Objectives</h2>
 * <ul>
 *   <li>Verify that {@link CustomerRepository#findByEmail(String)} returns matching customers.</li>
 *   <li>Validate the JPQL implementation of {@link CustomerRepository#search(String)}.</li>
 * </ul>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DataJpaTest
@DisplayName("CustomerRepository JPA Integration Tests")
class CustomerRepositoryIT {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Helper to construct and persist a {@link Customer}.
     */
    private void createCustomer(String username, String email, String firstName, String lastName) {
        Customer c = new Customer();
        c.setUsername(username);
        c.setEmail(email);
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setPhone("1234567890");
        customerRepository.save(c);
    }

    @Nested
    @DisplayName("findByEmail() tests")
    class FindByEmailTests {

        /**
         * Test: Should return customer when email exists.
         */
        @Test
        @DisplayName("Should find customer by exact email")
        void findByEmail_existing_returnsCustomer() {
            createCustomer("john", "john@example.com", "John", "Doe");

            Optional<Customer> found = customerRepository.findByEmail("john@example.com");

            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("john");
        }

        /**
         * Test: Should return empty when email does not exist.
         */
        @Test
        @DisplayName("Should return empty when email does not exist")
        void findByEmail_missing_returnsEmpty() {
            Optional<Customer> found = customerRepository.findByEmail("missing@example.com");

            assertThat(found).isNotPresent();
        }
    }

    @Nested
    @DisplayName("search() tests")
    class SearchTests {

        /**
         * Test: Should match by username, email, first name, or last name (case-insensitive).
         */
        @Test
        @DisplayName("Should search customers by keyword across multiple fields")
        void search_matchesAcrossFields() {
            createCustomer("johnny", "john@example.com", "John", "Doe");
            createCustomer("jane_d", "jane@example.com", "Jane", "Doe");
            createCustomer("other", "other@example.com", "Other", "Person");

            List<Customer> results = customerRepository.search("jane");

            assertThat(results)
                    .extracting(Customer::getEmail)
                    .containsExactlyInAnyOrder("jane@example.com");
        }

        /**
         * Test: Should return an empty list when no fields match the keyword.
         */
        @Test
        @DisplayName("Should return empty list when no match")
        void search_noMatch_returnsEmpty() {
            createCustomer("johnny", "john@example.com", "John", "Doe");

            List<Customer> results = customerRepository.search("zzz");

            assertThat(results).isEmpty();
        }

        /**
         * Test: Should be case-insensitive.
         */
        @Test
        @DisplayName("Should be case-insensitive")
        void search_isCaseInsensitive() {
            createCustomer("JohnUser", "JOHN@EXAMPLE.COM", "John", "Doe");

            List<Customer> results = customerRepository.search("john");

            assertThat(results).hasSize(1);
            assertThat(results.getFirst().getEmail()).isEqualTo("JOHN@EXAMPLE.COM");
        }
    }
}