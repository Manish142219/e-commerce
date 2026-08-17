package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByGenderSection(String genderSection);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
           "AND (:brand IS NULL OR p.brand = :brand) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:color IS NULL OR :color MEMBER OF p.colors) " +
           "AND (:minDiscount IS NULL OR p.discountPercent >= :minDiscount)")
    List<Product> filterProducts(
            @Param("categoryId") Long categoryId,
            @Param("brand") String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("color") String color,
            @Param("minDiscount") Integer minDiscount);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.category.id = :categoryId")
    List<String> findDistinctBrandsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);
}
