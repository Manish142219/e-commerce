package com.ecommerce.repository;

import com.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByDisplayOrderAsc();
    Optional<Category> findBySlug(String slug);
    List<Category> findByParentNav(String parentNav);
}
