package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavMenuDto {
    private String section;
    private List<CategoryDto> categories;
    private Map<Integer, List<NavMenuGroupDto>> columns;
}
