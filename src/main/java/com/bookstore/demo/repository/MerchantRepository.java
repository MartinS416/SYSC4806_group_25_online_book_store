package com.bookstore.demo.repository;

import com.bookstore.demo.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Feature --> Extend architecture to accommodate multiple merchants/ stores.
 */
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
