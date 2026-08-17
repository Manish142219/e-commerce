package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.entity.NavMenuItem;
import com.ecommerce.repository.NavMenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavMenuService {

    private final NavMenuItemRepository navMenuItemRepository;
    private final CategoryService categoryService;

    public NavMenuDto getNavMenu(String section) {
        String sectionKey = section == null ? "" : section.toUpperCase();

        List<CategoryDto> categories = categoryService.getCategoriesByNav(sectionKey);

        List<NavMenuItem> items = navMenuItemRepository
                .findByNavSectionOrderByColumnIndexAscDisplayOrderAsc(sectionKey);

        Map<Integer, Map<String, List<NavMenuLinkDto>>> columnGroups = new TreeMap<>();

        for (NavMenuItem item : items) {
            String slug = item.getLinkSlug();
            if (slug == null || slug.isBlank()) {
                slug = guessSlug(item.getItemName());
            }
            String linkType = item.getLinkType() != null ? item.getLinkType() : "CATEGORY";

            columnGroups
                    .computeIfAbsent(item.getColumnIndex(), k -> new LinkedHashMap<>())
                    .computeIfAbsent(item.getGroupTitle(), k -> new ArrayList<>())
                    .add(NavMenuLinkDto.builder()
                            .name(item.getItemName())
                            .slug(slug)
                            .linkType(linkType)
                            .build());
        }

        // If DB has no menu rows for this section, build a useful fallback from categories
        if (columnGroups.isEmpty() && !categories.isEmpty()) {
            List<NavMenuLinkDto> links = categories.stream()
                    .map(c -> NavMenuLinkDto.builder()
                            .name(c.getName())
                            .slug(c.getSlug())
                            .linkType("CATEGORY")
                            .build())
                    .collect(Collectors.toList());
            columnGroups.put(1, Map.of("Shop by Category", links));
        }

        Map<Integer, List<NavMenuGroupDto>> columns = new TreeMap<>();
        columnGroups.forEach((colIndex, groups) -> {
            List<NavMenuGroupDto> groupDtos = groups.entrySet().stream()
                    .map(e -> NavMenuGroupDto.builder()
                            .title(e.getKey())
                            .links(e.getValue())
                            .build())
                    .collect(Collectors.toList());
            columns.put(colIndex, groupDtos);
        });

        return NavMenuDto.builder()
                .section(sectionKey)
                .categories(categories)
                .columns(columns)
                .build();
    }

    public List<String> getNavSections() {
        return List.of("MEN", "WOMEN", "KIDS", "HOME", "BEAUTY", "GENZ", "STUDIO");
    }

    private String guessSlug(String itemName) {
        if (itemName == null) return "casual-wear";
        String n = itemName.toLowerCase();
        if (n.contains("shoe") || n.contains("footwear") || n.contains("sneaker")) return "footwear";
        if (n.contains("watch")) return "watches";
        if (n.contains("kurta") || n.contains("ethnic") || n.contains("saree")) return "ethnic-wear";
        if (n.contains("dress") || n.contains("top") || n.contains("jean") || n.contains("western")) return "western-wear";
        if (n.contains("makeup") || n.contains("skin") || n.contains("lipstick") || n.contains("beauty")) return "beauty-products";
        if (n.contains("beard") || n.contains("face wash") || n.contains("groom")) return "grooming";
        if (n.contains("active") || n.contains("sport") || n.contains("track")) return "mens-activewear";
        if (n.contains("sleep") || n.contains("lounge")) return "sleepwear";
        if (n.contains("inner") || n.contains("brief") || n.contains("vest")) return "innerwear";
        if (n.contains("lingerie") || n.contains("bra")) return "lingerie";
        return "casual-wear";
    }
}
