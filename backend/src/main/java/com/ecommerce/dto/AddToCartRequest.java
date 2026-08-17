package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Size is required")
    private String size;

    private Integer quantity = 1;
}
