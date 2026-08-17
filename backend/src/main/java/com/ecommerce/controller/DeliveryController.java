package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.DeliveryCheckDto;
import com.ecommerce.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<DeliveryCheckDto>> checkDelivery(@RequestParam String pincode) {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.checkDelivery(pincode)));
    }
}
