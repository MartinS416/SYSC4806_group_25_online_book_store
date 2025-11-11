package com.example.demo.repository;

import com.example.demo.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Feature --> Extend architecture to accommodate multiple merchants/ stores.
 */
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
