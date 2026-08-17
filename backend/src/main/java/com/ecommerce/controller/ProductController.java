package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Integer minDiscount) {

        List<ProductDto> products = productService.filterProducts(
                categoryId, brand, minPrice, maxPrice, color, minDiscount);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/category/{categoryId}/brands")
    public ResponseEntity<ApiResponse<List<String>>> getBrands(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getBrandsByCategory(categoryId)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDto>>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchProducts(q)));
    }
}
