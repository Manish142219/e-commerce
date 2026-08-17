package com.ecommerce.repository;

import com.ecommerce.entity.NavMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NavMenuItemRepository extends JpaRepository<NavMenuItem, Long> {
    List<NavMenuItem> findByNavSectionOrderByColumnIndexAscDisplayOrderAsc(String navSection);
}
