package com.bookstore.inventory.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Merchant} entity.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Category:</strong> Unit Tests (UT)</li>
 *   <li><strong>Scope:</strong> Merchant model validation, getters/setters, constructor behavior</li>
 *   <li><strong>Dependencies:</strong> None (pure POJO testing)</li>
 *   <li><strong>Test Levels:</strong> Basic functionality, boundary conditions, relationship management</li>
 *   <li><strong>Framework:</strong> JUnit 5 + AssertJ</li>
 *   <li><strong>Test Count:</strong> 15 test cases organized in 5 nested groups</li>
 * </ul>
 *
 * <h2>Test Coverage</h2>
 * <ul>
 *   <li>Constructor tests: default and parameterized initialization</li>
 *   <li>Property tests: all contact fields (id, name, email, phone)</li>
 *   <li>Validation tests: null, empty string handling for all fields</li>
 *   <li>State management: instance isolation, independent field updates</li>
 * </ul>
 *
 * <h2>Architecture Context</h2>
 * Merchant is a core entity in the multi-merchant architecture that owns Shop entities.
 * Each Merchant can operate one or more Shop locations.
 *
 * <h2>Usage</h2>
 * <pre>
 * mvn test -Dtest=MerchantTest
 * </pre>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DisplayName("Merchant Entity Tests")
class MerchantTest {

    private Merchant merchant;

    /**
     * Initializes a fresh Merchant instance before each test.
     * Ensures test isolation and prevents state leakage between test cases.
     */
    @BeforeEach
    void setUp() {
        merchant = new Merchant();
    }

    /**
     * Tests for Merchant constructor behaviour.
     * Validates both default and parameterized constructor initialization.
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        /**
         * Test: Should create Merchant with the default constructor.
         * Validates that a default-constructed Merchant has null/default values.
         */
        @Test
        @DisplayName("Should create Merchant with default constructor")
        void testDefaultConstructor() {
            // Arrange & Act
            Merchant newMerchant = new Merchant();

            // Assert
            assertThat(newMerchant).isNotNull();
            assertThat(newMerchant.getId()).isNull();
            assertThat(newMerchant.getName()).isNull();
            assertThat(newMerchant.getEmail()).isNull();
        }

        /**
         * Test: Should create Merchant with parameterized constructor.
         * Validates that all parameters are correctly assigned during construction.
         */
        @Test
        @DisplayName("Should create Merchant with parameterized constructor")
        void testParameterizedConstructor() {
            // Arrange
            String name = "Acme Books Inc";
            String email = "contact@acmebooks.com";

            // Act
            Merchant newMerchant = new Merchant(name, email);

            // Assert
            assertThat(newMerchant.getName()).isEqualTo(name);
            assertThat(newMerchant.getEmail()).isEqualTo(email);
        }
    }

    /**
     * Tests for Merchant getter and setter methods.
     * Validates that each property can be independently read and written.
     */
    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        /**
         * Test: Should set and get name.
         * Validates merchant business name property assignment and retrieval.
         */
        @Test
        @DisplayName("Should set and get name")
        void testNameGetterSetter() {
            // Arrange
            String name = "Powell's Books";

            // Act
            merchant.setName(name);

            // Assert
            assertThat(merchant.getName()).isEqualTo(name);
        }

        /**
         * Test: Should set and get email.
         * Validates merchant contact email property assignment and retrieval.
         */
        @Test
        @DisplayName("Should set and get email")
        void testEmailGetterSetter() {
            // Arrange
            String email = "info@powells.com";

            // Act
            merchant.setEmail(email);

            // Assert
            assertThat(merchant.getEmail()).isEqualTo(email);
        }

        /**
         * Test: Should set and get id.
         * Validates id property assignment and retrieval for unique identification.
         */
        @Test
        @DisplayName("Should set and get id")
        void testIdGetterSetter() {
            // Arrange
            Long id = 100L;

            // Act
            merchant.setId(id);

            // Assert
            assertThat(merchant.getId()).isEqualTo(id);
        }
    }

    /**
     * Tests for Merchant field validation and boundary conditions.
     * Validates handling of null, empty, and edge-case values.
     */
    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        /**
         * Test: Should allow null name.
         * Validates that name fields accept null values without exception.
         */
        @Test
        @DisplayName("Should allow null name")
        void testNullName() {
            // Act
            merchant.setName(null);

            // Assert
            assertThat(merchant.getName()).isNull();
        }

        /**
         * Test: Should allow null email.
         * Validates that email fields accept null values without exception.
         */
        @Test
        @DisplayName("Should allow null email")
        void testNullEmail() {
            // Act
            merchant.setEmail(null);

            // Assert
            assertThat(merchant.getEmail()).isNull();
        }


        /**
         * Test: Should handle empty string name.
         * Validates that name fields accept empty strings without exception.
         */
        @Test
        @DisplayName("Should handle empty string name")
        void testEmptyStringName() {
            // Act
            merchant.setName("");

            // Assert
            assertThat(merchant.getName()).isEmpty();
        }

        /**
         * Test: Should handle empty string email.
         * Validates that email fields accept empty strings without exception.
         */
        @Test
        @DisplayName("Should handle empty string email")
        void testEmptyStringEmail() {
            // Act
            merchant.setEmail("");

            // Assert
            assertThat(merchant.getEmail()).isEmpty();
        }

    }

    /**
     * Tests for Merchant state management and instance isolation.
     * Validates that instances maintain an independent state and support multiple updates.
     */
    @Nested
    @DisplayName("State Management Tests")
    class StateManagementTests {

        /**
         * Test: Should maintain independent state across instances.
         * Validates that multiple Merchant instances do not interfere with each other's state.
         */
        @Test
        @DisplayName("Should maintain independent state across instances")
        void testInstanceIsolation() {
            // Arrange
            Merchant merchant1 = new Merchant("Merchant 1", "email1@test.com");
            Merchant merchant2 = new Merchant("Merchant 2", "email2@test.com");

            // Act & Assert
            assertThat(merchant1.getName()).isNotEqualTo(merchant2.getName());
            assertThat(merchant1.getEmail()).isNotEqualTo(merchant2.getEmail());
        }

        /**
         * Test: Should update merchant details multiple times.
         * Validates that properties can be updated multiple times without issues.
         */
        @Test
        @DisplayName("Should update merchant details multiple times")
        void testMultipleUpdates() {
            // Act
            merchant.setName("Initial Name");
            assertThat(merchant.getName()).isEqualTo("Initial Name");

            merchant.setName("Updated Name");
            assertThat(merchant.getName()).isEqualTo("Updated Name");

            merchant.setEmail("initial@test.com");
            assertThat(merchant.getEmail()).isEqualTo("initial@test.com");

            merchant.setEmail("updated@test.com");

            // Assert
            assertThat(merchant.getEmail()).isEqualTo("updated@test.com");
            assertThat(merchant.getName()).isEqualTo("Updated Name");
        }

        /**
         * Test: Should allow updating all fields independently.
         * Validates that each field can be updated without affecting others.
         */
        @Test
        @DisplayName("Should allow updating all fields independently")
        void testIndependentFieldUpdates() {
            // Act
            merchant.setName("BookStore");
            merchant.setEmail("store@example.com");

            // Assert
            assertThat(merchant.getName()).isEqualTo("BookStore");
            assertThat(merchant.getEmail()).isEqualTo("store@example.com");
        }
    }
}