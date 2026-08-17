package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String brand;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal mrp;
    private Integer discountPercent;
    private String imageUrl;
    private List<String> images;
    private List<String> sizes;
    private List<String> colors;
    private Double rating;
    private Integer ratingCount;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String genderSection;
    /** CLOTHING | FOOTWEAR | BEAUTY | ACCESSORY */
    private String variantType;
    /** UI label e.g. SELECT SIZE / SELECT QUANTITY / SELECT VOLUME */
    private String variantLabel;
}
