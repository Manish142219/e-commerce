package com.ecommerce.service;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDto(product);
    }

    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> filterProducts(Long categoryId, String brand, BigDecimal minPrice,
                                           BigDecimal maxPrice, String color, Integer minDiscount) {
        return productRepository.filterProducts(categoryId, brand, minPrice, maxPrice, color, minDiscount)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<String> getBrandsByCategory(Long categoryId) {
        return productRepository.findDistinctBrandsByCategoryId(categoryId);
    }

    public List<ProductDto> searchProducts(String query) {
        return productRepository.searchProducts(query).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ProductDto toDto(Product product) {
        String variantType = product.getCategory() != null && product.getCategory().getVariantType() != null
                ? product.getCategory().getVariantType()
                : "CLOTHING";

        return ProductDto.builder()
                .id(product.getId())
                .brand(product.getBrand())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .mrp(product.getMrp())
                .discountPercent(product.getDiscountPercent())
                .imageUrl(product.getImageUrl())
                .images(product.getImages())
                .sizes(product.getSizes())
                .colors(product.getColors())
                .rating(product.getRating())
                .ratingCount(product.getRatingCount())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categorySlug(product.getCategory() != null ? product.getCategory().getSlug() : null)
                .genderSection(product.getGenderSection())
                .variantType(variantType)
                .variantLabel(com.ecommerce.util.VariantTypeUtil.labelFor(variantType))
                .build();
    }
}
