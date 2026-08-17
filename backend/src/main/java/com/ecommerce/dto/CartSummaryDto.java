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
public class CartSummaryDto {

    private List<CartItemDto> items;
    private BigDecimal totalMrp;
    private BigDecimal totalDiscount;
    private BigDecimal couponDiscount;
    private BigDecimal platformFee;
    private BigDecimal totalAmount;
    private int itemCount;
    private int selectedItemCount;
    private String appliedCouponCode;
    private AddressDto deliveryAddress;
}
