package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartSummaryDto>> getCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String pincode) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.getCart(userDetails.getUsername(), pincode)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemDto>> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        try {
            CartItemDto item = cartService.addToCart(userDetails.getUsername(), request);
            return ResponseEntity.ok(ApiResponse.success("Added to bag", item));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        cartService.updateQuantity(userDetails.getUsername(), id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Quantity updated", null));
    }

    @PutMapping("/{id}/select")
    public ResponseEntity<ApiResponse<Void>> toggleSelection(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam boolean selected) {
        cartService.toggleSelection(userDetails.getUsername(), id, selected);
        return ResponseEntity.ok(ApiResponse.success("Selection updated", null));
    }

    @PutMapping("/select-all")
    public ResponseEntity<ApiResponse<Void>> toggleAllSelection(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam boolean selected) {
        cartService.toggleAllSelection(userDetails.getUsername(), selected);
        return ResponseEntity.ok(ApiResponse.success("Selection updated", null));
    }

    @PostMapping("/coupon")
    public ResponseEntity<ApiResponse<CartSummaryDto>> applyCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApplyCouponRequest request) {
        try {
            CartSummaryDto cart = cartService.applyCoupon(
                    userDetails.getUsername(), request.getCouponCode());
            return ResponseEntity.ok(ApiResponse.success("Coupon applied", cart));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<ApiResponse<CartSummaryDto>> removeCoupon(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                "Coupon removed", cartService.removeCoupon(userDetails.getUsername())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        cartService.removeFromCart(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Removed from bag", null));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCartCount(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartCount(userDetails.getUsername())));
    }
}
