package com.ecommerce.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Fixes leftover/incompatible DB columns from older schema experiments.
 * Runs BEFORE DataSeeder so product inserts do not fail.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class SchemaFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        fixProductSizesTable();
        fixProductColorsTable();
    }

    private void fixProductSizesTable() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = 'product_sizes' " +
                            "AND COLUMN_NAME = 'size_label'",
                    Integer.class);

            if (count != null && count > 0) {
                // Drop leftover column that is NOT NULL and has no default
                jdbcTemplate.execute("ALTER TABLE product_sizes DROP COLUMN size_label");
                log.info("Dropped leftover column product_sizes.size_label");
            }
        } catch (Exception e) {
            log.warn("Could not fix product_sizes.size_label: {}", e.getMessage());
            // Fallback: try make it nullable with default
            try {
                jdbcTemplate.execute(
                        "ALTER TABLE product_sizes MODIFY COLUMN size_label VARCHAR(255) NULL DEFAULT NULL");
                log.info("Made product_sizes.size_label nullable as fallback");
            } catch (Exception ignored) {
                // ignore
            }
        }

        // Ensure size_value exists (Hibernate ElementCollection column)
        try {
            Integer sizeValueCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = 'product_sizes' " +
                            "AND COLUMN_NAME = 'size_value'",
                    Integer.class);
            if (sizeValueCount != null && sizeValueCount == 0) {
                jdbcTemplate.execute(
                        "ALTER TABLE product_sizes ADD COLUMN size_value VARCHAR(255) NULL");
                log.info("Added missing column product_sizes.size_value");
            }
        } catch (Exception e) {
            log.warn("Could not ensure size_value column: {}", e.getMessage());
        }
    }

    private void fixProductColorsTable() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = 'product_colors' " +
                            "AND COLUMN_NAME = 'color_label'",
                    Integer.class);
            if (count != null && count > 0) {
                jdbcTemplate.execute("ALTER TABLE product_colors DROP COLUMN color_label");
                log.info("Dropped leftover column product_colors.color_label");
            }
        } catch (Exception ignored) {
            // ignore
        }
    }
}
