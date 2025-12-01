package com.bookstore.inventory.service;

import com.bookstore.inventory.model.Merchant;
import com.bookstore.inventory.repository.MerchantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for {@link MerchantService}.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Type:</strong> Integration Test (IT)</li>
 *   <li><strong>Layer:</strong> Service layer and persistence</li>
 *   <li><strong>Scope:</strong> CRUD operations for {@link Merchant}</li>
 *   <li><strong>Dependencies:</strong> {@link MerchantRepository}, Spring Data JPA, H2 database</li>
 * </ul>
 *
 * <h2>Objectives</h2>
 * <ul>
 *   <li>Verify that MerchantService mediates access to MerchantRepository correctly.</li>
 *   <li>Ensure exception handling for missing merchants is correct.</li>
 *   <li>Support multi-merchant architecture by validating persistence and updates.</li>
 * </ul>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DataJpaTest
@Import(MerchantService.class)
@DisplayName("MerchantService Integration Tests")
class MerchantServiceIT {

    @Autowired
    private MerchantService merchantService;

    /**
     * Helper to create and persist a {@link Merchant}.
     */
    private Merchant createMerchant(String name, String email) {
        Merchant m = new Merchant();
        m.setName(name);
        m.setEmail(email);
        return merchantService.create(m);
    }

    @Nested
    @DisplayName("CRUD operations")
    class CrudTests {

        /**
         * Test: Should create and find merchant by id.
         */
        @Test
        @DisplayName("Should create and find merchant by id")
        void createAndFindById() {
            Merchant saved = createMerchant("Merchant A", "a@example.com");

            Merchant found = merchantService.findById(saved.getId());

            assertThat(found.getName()).isEqualTo("Merchant A");
            assertThat(found.getEmail()).isEqualTo("a@example.com");
        }

        /**
         * Test: Should throw when merchant not found.
         */
        @Test
        @DisplayName("Should throw when merchant not found")
        void findById_notFound() {
            assertThatThrownBy(() -> merchantService.findById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Merchant not found");
        }

        /**
         * Test: Should update merchant fields.
         */
        @Test
        @DisplayName("Should update merchant")
        void updateMerchant() {
            Merchant saved = createMerchant("Old Name", "old@example.com");

            Merchant updated = new Merchant();
            updated.setName("New Name");
            updated.setEmail("new@example.com");

            Merchant result = merchantService.update(saved.getId(), updated);

            assertThat(result.getName()).isEqualTo("New Name");
            assertThat(result.getEmail()).isEqualTo("new@example.com");
        }

        /**
         * Test: Should delete merchant.
         */
        @Test
        @DisplayName("Should delete merchant")
        void deleteMerchant() {
            Merchant saved = createMerchant("To Delete", "del@example.com");

            merchantService.delete(saved.getId());

            assertThatThrownBy(() -> merchantService.findById(saved.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Listing operations")
    class ListingTests {

        /**
         * Test: Should return all merchants.
         */
        @Test
        @DisplayName("Should list all merchants")
        void findAllMerchants() {
            createMerchant("Merchant A", "a@example.com");
            createMerchant("Merchant B", "b@example.com");

            List<Merchant> all = merchantService.findAll();

            assertThat(all)
                    .extracting(Merchant::getName)
                    .containsExactlyInAnyOrder("Merchant A", "Merchant B");
        }
    }
}