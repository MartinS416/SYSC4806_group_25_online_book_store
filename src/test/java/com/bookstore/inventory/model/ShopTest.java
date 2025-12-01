package com.bookstore.inventory.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Shop} entity.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Category:</strong> Unit Tests (UT)</li>
 *   <li><strong>Scope:</strong> Shop model validation, getters/setters, constructor behaviour, merchant relationship</li>
 *   <li><strong>Dependencies:</strong> Merchant (related entity)</li>
 *   <li><strong>Test Levels:</strong> Basic functionality, boundary conditions, relationship validation</li>
 *   <li><strong>Framework:</strong> JUnit 5 + AssertJ</li>
 *   <li><strong>Test Count:</strong> 18 test cases organized in 6 nested groups</li>
 * </ul>
 *
 * <h2>Test Coverage</h2>
 * <ul>
 *   <li>Constructor tests: default, parameterized, null merchant cases</li>
 *   <li>Property tests: all fields (id, name, merchant)</li>
 *   <li>Validation tests: null, empty string handling</li>
 *   <li>Relationship tests: merchant association, multi-shop scenarios</li>
 *   <li>State management: instance isolation and independent updates</li>
 *   <li>Feature validation: multi-merchant/multi-shop architecture support</li>
 * </ul>
 *
 * <h2>Architecture Context</h2>
 * Shop is a key part of the multi-merchant/multi-store feature.
 * Each Shop is owned by a Merchant and can contain multiple Book entities.
 * This represents the following feature requirement:
 * <blockquote>"Extend architecture to accommodate multiple merchants/stores"</blockquote>
 *
 * <h2>Requirement Traceability</h2>
 * <ul>
 *   <li><strong>Feature:</strong> Multi-merchant/multi-store support</li>
 *   <li><strong>Design:</strong> Shop ↔ Merchant ManyToOne relationship</li>
 *   <li><strong>Implementation:</strong> JPA entity with a foreign key to Merchant</li>
 *   <li><strong>Test Validation:</strong> Multi-Merchant Architecture Tests group</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>
 * mvn test -Dtest=ShopTest
 * mvn test -Dtest=ShopTest#testMultiMerchantMultiShop
 * </pre>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 * @see Merchant
 * @see Book
 */
@DisplayName("Shop Entity Tests")
class ShopTest {

    private Shop shop;
    private Merchant merchant;

    /**
     * Initializes a fresh Shop instance and test Merchant before each test.
     * Ensures test isolation and prevents state leakage between test cases.
     */
    @BeforeEach
    void setUp() {
        shop = new Shop();
        merchant = new Merchant("Test Merchant", "test@example.com");
    }

    /**
     * Tests for Shop constructor behaviour.
     * Validates default, parameterized, and edge-case constructor initialization.
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        /**
         * Test: Should create Shop with the default constructor.
         * Validates that a default-constructed Shop has null/default values.
         */
        @Test
        @DisplayName("Should create Shop with default constructor")
        void testDefaultConstructor() {
            // Arrange & Act
            Shop newShop = new Shop();

            // Assert
            assertThat(newShop).isNotNull();
            assertThat(newShop.getId()).isNull();
            assertThat(newShop.getName()).isNull();
            assertThat(newShop.getMerchant()).isNull();
        }

        /**
         * Test: Should create Shop with a parameterized constructor.
         * Validates that all parameters are correctly assigned during construction.
         */
        @Test
        @DisplayName("Should create Shop with parameterized constructor")
        void testParameterizedConstructor() {
            // Arrange
            String name = "Downtown Store";

            // Act
            Shop newShop = new Shop(name, merchant);

            // Assert
            assertThat(newShop.getName()).isEqualTo(name);
            assertThat(newShop.getMerchant()).isEqualTo(merchant);
        }

        /**
         * Test: Should create Shop with null merchant.
         * Validates that Shop can be created without merchant association (edge case).
         */
        @Test
        @DisplayName("Should create Shop with null merchant")
        void testConstructorWithNullMerchant() {
            // Act
            Shop newShop = new Shop("Unaffiliated Shop", null);

            // Assert
            assertThat(newShop.getName()).isEqualTo("Unaffiliated Shop");
            assertThat(newShop.getMerchant()).isNull();
        }
    }

    /**
     * Tests for Shop getter and setter methods.
     * Validates that each property can be independently read and written.
     */
    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        /**
         * Test: Should set and get name.
         * Validates shop name property assignment and retrieval.
         */
        @Test
        @DisplayName("Should set and get name")
        void testNameGetterSetter() {
            // Arrange
            String name = "Midtown Bookstore";

            // Act
            shop.setName(name);

            // Assert
            assertThat(shop.getName()).isEqualTo(name);
        }

        /**
         * Test: Should set and get id.
         * Validates id property assignment and retrieval for unique identification.
         */
        @Test
        @DisplayName("Should set and get id")
        void testIdGetterSetter() {
            // Arrange
            Long id = 1L;

            // Act
            shop.setId(id);

            // Assert
            assertThat(shop.getId()).isEqualTo(id);
        }

        /**
         * Test: Should set and get merchant.
         * Validates the ManyToOne relationship with Merchant entity.
         */
        @Test
        @DisplayName("Should set and get merchant")
        void testMerchantGetterSetter() {
            // Act
            shop.setMerchant(merchant);

            // Assert
            assertThat(shop.getMerchant()).isEqualTo(merchant);
        }
    }

    /**
     * Tests for Shop field validation and boundary conditions.
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
            shop.setName(null);

            // Assert
            assertThat(shop.getName()).isNull();
        }

        /**
         * Test: Should allow null merchant.
         * Validates that merchant field accepts null values without exception.
         */
        @Test
        @DisplayName("Should allow null merchant")
        void testNullMerchant() {
            // Act
            shop.setMerchant(null);

            // Assert
            assertThat(shop.getMerchant()).isNull();
        }

        /**
         * Test: Should handle empty string name.
         * Validates that name fields accept empty strings without exception.
         */
        @Test
        @DisplayName("Should handle empty string name")
        void testEmptyStringName() {
            // Act
            shop.setName("");

            // Assert
            assertThat(shop.getName()).isEmpty();
        }
    }

    /**
     * Tests for Shop-Merchant relationship management.
     * Validates bidirectional association behaviour and multi-shop scenarios.
     */
    @Nested
    @DisplayName("Merchant Relationship Tests")
    class MerchantRelationshipTests {

        /**
         * Test: Should associate shop with merchant.
         * Validates that a Shop can be properly associated with a Merchant.
         */
        @Test
        @DisplayName("Should associate shop with merchant")
        void testShopMerchantAssociation() {
            // Act
            shop.setName("Main Store");
            shop.setMerchant(merchant);

            // Assert
            assertThat(shop.getName()).isEqualTo("Main Store");
            assertThat(shop.getMerchant()).isEqualTo(merchant);
            assertThat(shop.getMerchant().getName()).isEqualTo("Test Merchant");
        }

        /**
         * Test: Should allow multiple shops under the same merchant.
         * Validates the one-to-many relationship: one Merchant → many Shops.
         * Feature: Multi-merchant/multi-shop architecture.
         */
        @Test
        @DisplayName("Should allow multiple shops under same merchant")
        void testMultipleShopsUnderMerchant() {
            // Arrange
            Shop shop1 = new Shop("Store 1", merchant);
            Shop shop2 = new Shop("Store 2", merchant);

            // Act & Assert
            assertThat(shop1.getMerchant()).isEqualTo(shop2.getMerchant());
            assertThat(shop1.getName()).isNotEqualTo(shop2.getName());
        }

        /**
         * Test: Should allow changing shop's merchant.
         * Validates that a Shop can be reassigned to a different Merchant.
         */
        @Test
        @DisplayName("Should allow changing shop's merchant")
        void testChangingMerchant() {
            // Arrange
            Merchant merchant2 = new Merchant("Second Merchant", "second@example.com");
            shop.setMerchant(merchant);
            assertThat(shop.getMerchant()).isEqualTo(merchant);

            // Act
            shop.setMerchant(merchant2);

            // Assert
            assertThat(shop.getMerchant()).isEqualTo(merchant2);
            assertThat(shop.getMerchant()).isNotEqualTo(merchant);
        }

        /**
         * Test: Should allow disassociating shop from merchant.
         * Validates that a Shop can be removed from a Merchant association.
         */
        @Test
        @DisplayName("Should allow disassociating shop from merchant")
        void testRemoveMerchant() {
            // Arrange
            shop.setMerchant(merchant);
            assertThat(shop.getMerchant()).isNotNull();

            // Act
            shop.setMerchant(null);

            // Assert
            assertThat(shop.getMerchant()).isNull();
        }
    }

    /**
     * Tests for Shop state management and instance isolation.
     * Validates that instances maintain an independent state and support multiple updates.
     */
    @Nested
    @DisplayName("State Management Tests")
    class StateManagementTests {

        /**
         * Test: Should maintain independent state across instances.
         * Validates that multiple Shop instances do not interfere with each other's state.
         */
        @Test
        @DisplayName("Should maintain independent state across instances")
        void testInstanceIsolation() {
            // Arrange
            Merchant merchant1 = new Merchant("Merchant 1", "m1@test.com");
            Merchant merchant2 = new Merchant("Merchant 2", "m2@test.com");
            Shop shop1 = new Shop("Shop 1", merchant1);
            Shop shop2 = new Shop("Shop 2", merchant2);

            // Act & Assert
            assertThat(shop1.getName()).isNotEqualTo(shop2.getName());
            assertThat(shop1.getMerchant()).isNotEqualTo(shop2.getMerchant());
        }

        /**
         * Test: Should update shop details multiple times.
         * Validates that properties can be updated multiple times without issues.
         */
        @Test
        @DisplayName("Should update shop details multiple times")
        void testMultipleUpdates() {
            // Act
            shop.setName("Store A");
            shop.setMerchant(merchant);
            assertThat(shop.getName()).isEqualTo("Store A");
            assertThat(shop.getMerchant()).isEqualTo(merchant);

            shop.setName("Store B");
            assertThat(shop.getName()).isEqualTo("Store B");

            Merchant merchant2 = new Merchant("New Merchant", "new@test.com");
            shop.setMerchant(merchant2);

            // Assert
            assertThat(shop.getName()).isEqualTo("Store B");
            assertThat(shop.getMerchant()).isEqualTo(merchant2);
        }

        /**
         * Test: Should handle all fields independently.
         * Validates that each field can be updated without affecting others.
         */
        @Test
        @DisplayName("Should handle all fields independently")
        void testIndependentFieldUpdates() {
            // Act
            shop.setId(5L);
            shop.setName("Premium Location");
            shop.setMerchant(merchant);

            // Assert
            assertThat(shop.getId()).isEqualTo(5L);
            assertThat(shop.getName()).isEqualTo("Premium Location");
            assertThat(shop.getMerchant()).isEqualTo(merchant);
        }
    }

    /**
     * Tests for multi-merchant/multi-shop architecture feature.
     * Validates the core requirement: "Extend architecture to accommodate multiple merchants/stores"
     * This test group is explicitly tied to feature requirements and V&V traceability.
     */
    @Nested
    @DisplayName("Multi-Merchant Architecture Tests")
    class MultiMerchantArchitectureTests {

        /**
         * Test: Should support feature - multiple merchants with multiple shops each.
         * Validates the complete multi-merchant architecture capability.
         * This test directly supports the stated feature requirement.
         */
        @Test
        @DisplayName("Should support feature: multiple merchants with multiple shops each")
        void testMultiMerchantMultiShop() {
            // Arrange
            Merchant merchant1 = new Merchant("Merchant A", "a@test.com");
            Merchant merchant2 = new Merchant("Merchant B", "b@test.com");

            Shop shopA1 = new Shop("Merchant A - Store 1", merchant1);
            Shop shopA2 = new Shop("Merchant A - Store 2", merchant1);
            Shop shopB1 = new Shop("Merchant B - Store 1", merchant2);

            // Act & Assert
            assertThat(shopA1.getMerchant()).isEqualTo(merchant1);
            assertThat(shopA2.getMerchant()).isEqualTo(merchant1);
            assertThat(shopB1.getMerchant()).isEqualTo(merchant2);
            assertThat(shopA1.getName()).contains("Store 1");
            assertThat(shopB1.getName()).contains("Merchant B");
        }

        /**
         * Test: Feature scope - Each merchant can have their own shop(s).
         * Validates that the Shop entity properly supports the merchant ownership model.
         * Feature requirement: Each Merchant independently owns and manages their Shop entities.
         */
        @Test
        @DisplayName("Feature scope: Each merchant can have their own shop(s)")
        void testMerchantShopOwnership() {
            // Arrange
            Shop shop1 = new Shop("First Shop", merchant);
            shop1.setId(1L);

            // Act
            assertThat(shop1.getMerchant().getName()).isEqualTo("Test Merchant");

            // Assert - verifies the feature requirement
            assertThat(shop1.getMerchant()).isNotNull();
            assertThat(shop1.getName()).startsWith("First");
        }
    }
}