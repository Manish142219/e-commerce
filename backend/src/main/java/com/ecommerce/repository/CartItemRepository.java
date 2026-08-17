package com.ecommerce.repository;

import com.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndProductIdAndSize(Long userId, Long productId, String size);
    int countByUserId(Long userId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
