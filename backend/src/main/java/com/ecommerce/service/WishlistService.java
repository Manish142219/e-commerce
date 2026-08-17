package com.ecommerce.service;

import com.ecommerce.dto.WishlistItemDto;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.entity.WishlistItem;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<WishlistItemDto> getWishlist(String email) {
        User user = getUser(email);
        return wishlistItemRepository.findByUserId(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public WishlistItemDto addToWishlist(String email, Long productId) {
        User user = getUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (wishlistItemRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new RuntimeException("Product already in wishlist");
        }

        WishlistItem item = WishlistItem.builder()
                .user(user)
                .product(product)
                .build();

        item = wishlistItemRepository.save(item);
        return toDto(item);
    }

    @Transactional
    public void removeFromWishlist(String email, Long productId) {
        User user = getUser(email);
        WishlistItem item = wishlistItemRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));
        wishlistItemRepository.delete(item);
    }

    public int getWishlistCount(String email) {
        User user = getUser(email);
        return wishlistItemRepository.countByUserId(user.getId());
    }

    public boolean isInWishlist(String email, Long productId) {
        User user = getUser(email);
        return wishlistItemRepository.existsByUserIdAndProductId(user.getId(), productId);
    }

    private WishlistItemDto toDto(WishlistItem item) {
        Product p = item.getProduct();
        return WishlistItemDto.builder()
                .id(item.getId())
                .productId(p.getId())
                .brand(p.getBrand())
                .name(p.getName())
                .imageUrl(p.getImageUrl())
                .price(p.getPrice())
                .mrp(p.getMrp())
                .discountPercent(p.getDiscountPercent())
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
