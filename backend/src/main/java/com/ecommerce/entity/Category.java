package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String slug;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "discount_text")
    private String discountText;

    @Column(name = "parent_nav")
    private String parentNav;

    @Column(name = "display_order")
    private Integer displayOrder;

    /**
     * CLOTHING  → apparel sizes (S/M/L or 38/40)
     * FOOTWEAR  → shoe sizes (UK 7/8/9)
     * BEAUTY    → quantity / volume (100ml, 200ml, Pack of 1)
     * ACCESSORY → free size / one size (watches, bags)
     */
    @Column(name = "variant_type")
    @Builder.Default
    private String variantType = "CLOTHING";
}
