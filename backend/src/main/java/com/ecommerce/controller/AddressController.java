package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressDto>>> getAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(addressService.getAddresses(userDetails.getUsername())));
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<AddressDto>> getDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails) {
        AddressDto address = addressService.getDefaultAddress(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressDto>> createAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAddressRequest request) {
        try {
            AddressDto address = addressService.createAddress(userDetails.getUsername(), request);
            return ResponseEntity.ok(ApiResponse.success("Address saved", address));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressDto>> setDefault(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                addressService.setDefaultAddress(userDetails.getUsername(), id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        addressService.deleteAddress(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted", null));
    }
}
