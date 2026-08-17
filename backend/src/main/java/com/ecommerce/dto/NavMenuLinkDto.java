package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavMenuLinkDto {
    private String name;
    private String slug;
    private String linkType; // CATEGORY | SEARCH
}
