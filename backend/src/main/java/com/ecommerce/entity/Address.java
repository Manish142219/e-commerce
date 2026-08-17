package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;
    private String phone;
    private String pincode;
    private String addressLine;
    private String city;
    private String state;

    @Builder.Default
    private String label = "HOME";

    @Column(name = "is_default")
    @Builder.Default
    private boolean isDefault = false;
}
