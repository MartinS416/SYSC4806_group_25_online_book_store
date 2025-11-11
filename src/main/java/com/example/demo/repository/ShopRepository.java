package com.example.demo.repository;

import com.example.demo.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Feature --> Extend architecture to have a merchant/ shop directory.
 */
public interface ShopRepository extends JpaRepository<Shop, Long> {
}
