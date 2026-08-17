package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.WishlistItemDto;
import com.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistItemDto>>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlist(userDetails.getUsername())));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<WishlistItemDto>> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        try {
            WishlistItemDto item = wishlistService.addToWishlist(userDetails.getUsername(), productId);
            return ResponseEntity.ok(ApiResponse.success("Added to wishlist", item));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist", null));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getWishlistCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlistCount(userDetails.getUsername())));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> checkWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
                wishlistService.isInWishlist(userDetails.getUsername(), productId)));
    }
}
