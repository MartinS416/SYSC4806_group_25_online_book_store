package com.bookstore.inventory.service;

import com.bookstore.inventory.model.Merchant;
import com.bookstore.inventory.model.Shop;
import com.bookstore.inventory.repository.MerchantRepository;
import com.bookstore.inventory.repository.ShopRepository;
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
 * Integration tests for {@link ShopService}.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Type:</strong> Integration Test (IT)</li>
 *   <li><strong>Layer:</strong> Service layer and persistence</li>
 *   <li><strong>Scope:</strong> CRUD operations for {@link Shop} and its association to {@link Merchant}</li>
 *   <li><strong>Dependencies:</strong> {@link ShopRepository}, {@link MerchantRepository}, Spring Data JPA, H2 DB</li>
 * </ul>
 *
 * <h2>Architecture Context</h2>
 * These tests validate the service layer behaviour for the
 * <em>\"Extend architecture to accommodate multiple merchants/stores\"</em> feature,
 * ensuring that shops are correctly created, updated, and linked to merchants.
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DataJpaTest
@Import({ShopService.class, MerchantService.class})
@DisplayName("ShopService Integration Tests")
class ShopServiceIT {

    @Autowired
    private ShopService shopService;

    @Autowired
    private MerchantService merchantService;

    /**
     * Helper to create and persist a {@link Merchant} via the service.
     */
    private Merchant createMerchant(String name) {
        Merchant m = new Merchant();
        m.setName(name);
        m.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        return merchantService.create(m);
    }

    /**
     * Helper to create and persist a {@link Shop} via the service.
     */
    private Shop createShop(String name, Merchant merchant) {
        Shop s = new Shop();
        s.setName(name);
        s.setMerchant(merchant);
        return shopService.create(s);
    }

    @Nested
    @DisplayName("CRUD operations")
    class CrudTests {

        /**
         * Test: Should create and find shop by id.
         */
        @Test
        @DisplayName("Should create and find shop by id")
        void createAndFindShop() {
            Merchant merchant = createMerchant("Merchant A");
            Shop saved = createShop("Downtown Store", merchant);

            Shop found = shopService.findById(saved.getId());

            assertThat(found.getName()).isEqualTo("Downtown Store");
            assertThat(found.getMerchant()).isNotNull();
            assertThat(found.getMerchant().getName()).isEqualTo("Merchant A");
        }

        /**
         * Test: Should throw when shop not found.
         */
        @Test
        @DisplayName("Should throw when shop not found")
        void findById_notFound() {
            assertThatThrownBy(() -> shopService.findById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Shop not found");
        }

        /**
         * Test: Should update shop name and merchant.
         */
        @Test
        @DisplayName("Should update shop")
        void updateShop() {
            Merchant m1 = createMerchant("Merchant A");
            Merchant m2 = createMerchant("Merchant B");
            Shop saved = createShop("Old Store", m1);

            Shop updated = new Shop();
            updated.setName("New Store");
            updated.setMerchant(m2);

            Shop result = shopService.update(saved.getId(), updated);

            assertThat(result.getName()).isEqualTo("New Store");
            assertThat(result.getMerchant().getName()).isEqualTo("Merchant B");
        }

        /**
         * Test: Should delete shop.
         */
        @Test
        @DisplayName("Should delete shop")
        void deleteShop() {
            Merchant merchant = createMerchant("Merchant A");
            Shop saved = createShop("To Delete", merchant);

            shopService.delete(saved.getId());

            assertThatThrownBy(() -> shopService.findById(saved.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Listing operations")
    class ListingTests {

        /**
         * Test: Should list all shops.
         */
        @Test
        @DisplayName("Should list all shops")
        void findAllShops() {
            Merchant m1 = createMerchant("Merchant A");
            Merchant m2 = createMerchant("Merchant B");

            createShop("A - Store 1", m1);
            createShop("B - Store 1", m2);

            List<Shop> all = shopService.findAll();

            assertThat(all)
                    .extracting(Shop::getName)
                    .containsExactlyInAnyOrder("A - Store 1", "B - Store 1");
        }
    }
}