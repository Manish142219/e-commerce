package com.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private Long id;
    private String name;
    private String phone;
    private String pincode;
    private String addressLine;
    private String city;
    private String state;
    @JsonProperty("isDefault")
    private boolean isDefault;
    private String label;
}
