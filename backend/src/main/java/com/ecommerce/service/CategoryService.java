package com.ecommerce.service;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.entity.Category;
import com.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return toDto(category);
    }

    public List<CategoryDto> getCategoriesByNav(String navSection) {
        return categoryRepository.findByParentNav(navSection)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .imageUrl(category.getImageUrl())
                .discountText(category.getDiscountText())
                .parentNav(category.getParentNav())
                .variantType(category.getVariantType() != null ? category.getVariantType() : "CLOTHING")
                .build();
    }
}
