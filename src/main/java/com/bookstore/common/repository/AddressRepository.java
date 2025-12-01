// AddressRepository.java
package com.bookstore.common.repository;

import com.bookstore.common.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for {@link Address} entities.
 *
 * Test Category: Integration Tests (IT) – persistence layer.
 * Scope: basic CRUD operations for addresses.
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-11-02
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
}