package com.bookstore.inventory.repository;

import com.bookstore.inventory.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link MerchantRepository}.
 *
 * <h2>Test Design Specification (TDS) Alignment</h2>
 * <ul>
 *   <li><strong>Test Type:</strong> Integration Test (IT)</li>
 *   <li><strong>Layer:</strong> Persistence / JPA repository</li>
 *   <li><strong>Scope:</strong> CRUD operations for {@link Merchant}</li>
 *   <li><strong>Dependencies:</strong> Spring Data JPA, H2 in-memory database</li>
 * </ul>
 *
 * <h2>Objectives</h2>
 * <ul>
 *   <li>Verify that basic save, find, and delete operations work correctly.</li>
 *   <li>Support the multi-merchant architecture by ensuring merchants persist correctly.</li>
 * </ul>
 *
 * <h2>Execution</h2>
 * <pre>
 * mvn test -Dtest=MerchantRepositoryIT
 * </pre>
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DataJpaTest
@DisplayName("MerchantRepository JPA Integration Tests")
class MerchantRepositoryIT {

    @Autowired
    private MerchantRepository merchantRepository;

    /**
     * Utility helper to construct and persist a {@link Merchant} for test setup.
     *
     * @param name  merchant name
     * @param email merchant email
     * @return persisted Merchant instance
     */
    private Merchant createMerchant(String name, String email) {
        Merchant m = new Merchant();
        m.setName(name);
        m.setEmail(email);
        return merchantRepository.save(m);
    }

    /**
     * Test: Should save and load merchant by id.
     * Validates primary key generation and retrieval.
     */
    @Test
    @DisplayName("Should save and load merchant by id")
    void saveAndFindById() {
        Merchant saved = createMerchant("Merchant A", "a@example.com");

        Optional<Merchant> found = merchantRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Merchant A");
        assertThat(found.get().getEmail()).isEqualTo("a@example.com");
    }

    /**
     * Test: Should find all merchants.
     * Validates retrieval of multiple records.
     */
    @Test
    @DisplayName("Should find all merchants")
    void findAllMerchants() {
        createMerchant("Merchant A", "a@example.com");
        createMerchant("Merchant B", "b@example.com");

        List<Merchant> all = merchantRepository.findAll();

        assertThat(all)
                .extracting(Merchant::getName)
                .containsExactlyInAnyOrder("Merchant A", "Merchant B");
    }

    /**
     * Test: Should delete a merchant.
     * Confirms that the delete operation removes the row from the database.
     */
    @Test
    @DisplayName("Should delete merchant")
    void deleteMerchant() {
        Merchant saved = createMerchant("Merchant A", "a@example.com");

        merchantRepository.delete(saved);

        assertThat(merchantRepository.findById(saved.getId())).isNotPresent();
    }
}