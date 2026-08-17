package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCheckDto {

    private String pincode;
    private boolean deliverable;
    private String estimatedDeliveryDate;
    private boolean codAvailable;
    private int returnDays;
    private String message;
    private String city;
}
