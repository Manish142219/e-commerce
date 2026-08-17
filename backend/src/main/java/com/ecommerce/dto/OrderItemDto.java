package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long productId;
    private String brand;
    private String name;
    private String imageUrl;
    private String size;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal mrp;
}
