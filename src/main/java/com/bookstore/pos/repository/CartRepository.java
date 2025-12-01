package com.bookstore.pos.repository;

import com.bookstore.pos.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // Find the active cart for a given customer
    Optional<Cart> findByCustomerIdAndActiveTrue(Long customerId);
}
