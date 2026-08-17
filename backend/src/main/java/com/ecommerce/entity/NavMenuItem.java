package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nav_menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NavMenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nav_section", nullable = false)
    private String navSection;

    @Column(name = "column_index")
    private Integer columnIndex;

    @Column(name = "group_title")
    private String groupTitle;

    @Column(name = "item_name")
    private String itemName;

    /** category slug or search keyword */
    @Column(name = "link_slug")
    private String linkSlug;

    /** CATEGORY or SEARCH */
    @Column(name = "link_type")
    @Builder.Default
    private String linkType = "CATEGORY";

    @Column(name = "display_order")
    private Integer displayOrder;
}
