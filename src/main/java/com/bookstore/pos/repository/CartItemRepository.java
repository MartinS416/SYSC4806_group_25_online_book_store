package com.bookstore.pos.repository;

import com.bookstore.pos.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartIdAndBookId(Long cartId, Long bookId);
    void deleteByCartId(Long cartId);
}