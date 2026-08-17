package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrders(userDetails.getUsername())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrder(userDetails.getUsername(), id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PlaceOrderRequest request) {
        try {
            OrderDto order = orderService.placeOrder(userDetails.getUsername(), request);
            return ResponseEntity.ok(ApiResponse.success("Order placed successfully", order));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
