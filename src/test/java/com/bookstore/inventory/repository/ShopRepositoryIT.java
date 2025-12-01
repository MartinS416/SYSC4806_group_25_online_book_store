package com.bookstore.inventory.repository;

import com.bookstore.inventory.model.Merchant;
import com.bookstore.inventory.model.Shop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ShopRepository}.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Type:</strong> Integration Test (IT)</li>
 *   <li><strong>Layer:</strong> Persistence / JPA repository</li>
 *   <li><strong>Scope:</strong> Persistence of {@link Shop} and its relationship to {@link Merchant}</li>
 *   <li><strong>Dependencies:</strong> Spring Data JPA, H2 in-memory database, JPA mappings</li>
 * </ul>
 *
 * <h2>Architecture Context</h2>
 * These tests validate the persistence aspect of the
 * <em>\"Extend architecture to accommodate multiple merchants/stores\"</em> feature
 * by ensuring that a Merchant can own multiple Shops and that relationships persist correctly.
 *
 * <h2>Execution</h2>
 * <pre>
 * mvn test -Dtest=ShopRepositoryIT
 * </pre>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DataJpaTest
@DisplayName("ShopRepository JPA Integration Tests")
class ShopRepositoryIT {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    /**
     * Utility helper to create and persist a {@link Merchant}.
     *
     * @param name merchant name
     * @return persisted Merchant
     */
    private Merchant createMerchant(String name) {
        Merchant m = new Merchant();
        m.setName(name);
        m.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        return merchantRepository.save(m);
    }

    /**
     * Utility helper to create and persist a {@link Shop}.
     *
     * @param name     shop name
     * @param merchant owning merchant
     * @return persisted Shop
     */
    private Shop createShop(String name, Merchant merchant) {
        Shop s = new Shop();
        s.setName(name);
        s.setMerchant(merchant);
        return shopRepository.save(s);
    }

    /**
     * Basic persistence tests for Shop entity.
     */
    @Nested
    @DisplayName("Basic persistence tests")
    class BasicPersistenceTests {

        /**
         * Test: Should save and load shop with merchant.
         * Validates that the ManyToOne association is persisted and rehydrated.
         */
        @Test
        @DisplayName("Should save and load shop with merchant")
        void saveAndLoadShop() {
            Merchant merchant = createMerchant("Merchant A");
            Shop saved = createShop("Downtown Store", merchant);

            Shop found = shopRepository.findById(saved.getId()).orElseThrow();

            assertThat(found.getName()).isEqualTo("Downtown Store");
            assertThat(found.getMerchant()).isNotNull();
            assertThat(found.getMerchant().getName()).isEqualTo("Merchant A");
        }

        /**
         * Test: Should find all shops for multiple merchants.
         * Confirms that all persisted shops are returned regardless of merchant.
         */
        @Test
        @DisplayName("Should find all shops for multiple merchants")
        void findAllShopsMultiMerchant() {
            Merchant m1 = createMerchant("Merchant A");
            Merchant m2 = createMerchant("Merchant B");

            createShop("A - Store 1", m1);
            createShop("A - Store 2", m1);
            createShop("B - Store 1", m2);

            List<Shop> all = shopRepository.findAll();

            assertThat(all).hasSize(3);
            assertThat(all)
                    .extracting(Shop::getName)
                    .containsExactlyInAnyOrder("A - Store 1", "A - Store 2", "B - Store 1");
        }
    }

    /**
     * Tests specifically targeting the multi-merchant / multi-shop architecture.
     */
    @Nested
    @DisplayName("Multi-merchant architecture persistence tests")
    class MultiMerchantArchitecturePersistenceTests {

        /**
         * Test: Should persist a one-to-many Merchant → Shops relationship.
         * Validates that multiple shops can share the same merchant foreign key.
         */
        @Test
        @DisplayName("Should persist one-to-many Merchant → Shops relationship")
        void persistMerchantWithMultipleShops() {
            Merchant merchant = createMerchant("Merchant A");

            createShop("Store 1", merchant);
            createShop("Store 2", merchant);

            List<Shop> shops = shopRepository.findAll();

            assertThat(shops).hasSize(2);
            assertThat(shops)
                    .extracting(Shop::getMerchant)
                    .allMatch(m -> m.getId().equals(merchant.getId()));
        }
    }
}