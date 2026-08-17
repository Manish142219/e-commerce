package com.ecommerce.config;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.NavMenuItem;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.NavMenuItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.util.VariantTypeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final NavMenuItemRepository navMenuItemRepository;

    private static final Map<String, String> VARIANT_BY_SLUG = Map.ofEntries(
            Map.entry("ethnic-wear", VariantTypeUtil.CLOTHING),
            Map.entry("casual-wear", VariantTypeUtil.CLOTHING),
            Map.entry("mens-activewear", VariantTypeUtil.CLOTHING),
            Map.entry("womens-activewear", VariantTypeUtil.CLOTHING),
            Map.entry("western-wear", VariantTypeUtil.CLOTHING),
            Map.entry("sportswear", VariantTypeUtil.CLOTHING),
            Map.entry("sleepwear", VariantTypeUtil.CLOTHING),
            Map.entry("innerwear", VariantTypeUtil.CLOTHING),
            Map.entry("lingerie", VariantTypeUtil.CLOTHING),
            Map.entry("footwear", VariantTypeUtil.FOOTWEAR),
            Map.entry("watches", VariantTypeUtil.ACCESSORY),
            Map.entry("grooming", VariantTypeUtil.BEAUTY),
            Map.entry("beauty-products", VariantTypeUtil.BEAUTY)
    );

    @Override
    @Transactional
    public void run(String... args) {
        boolean firstRun = categoryRepository.count() == 0;
        if (firstRun) {
            seedCategories();
            seedProducts();
            seedNavMenus();
        } else {
            ensureFootwearCategory();
            backfillVariantTypes();
            ensureExtraProducts();
            ensureNavMenus();
        }
    }

    private void seedCategories() {
        List<Category> categories = Arrays.asList(
                cat("Ethnic Wear", "ethnic-wear", "https://images.unsplash.com/photo-1610037129982-bef02da415e4?w=400", "50-80% OFF", "MEN", 1, VariantTypeUtil.CLOTHING),
                cat("Casual Wear", "casual-wear", "https://images.unsplash.com/photo-1596755094514-f87e34085b56?w=400", "40-80% OFF", "MEN", 2, VariantTypeUtil.CLOTHING),
                cat("Men's Activewear", "mens-activewear", "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400", "30-70% OFF", "MEN", 3, VariantTypeUtil.CLOTHING),
                cat("Women's Activewear", "womens-activewear", "https://images.unsplash.com/photo-1518310383802-640c2de311b2?w=400", "30-70% OFF", "WOMEN", 4, VariantTypeUtil.CLOTHING),
                cat("Western Wear", "western-wear", "https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=400", "40-80% OFF", "WOMEN", 5, VariantTypeUtil.CLOTHING),
                cat("Sportswear", "sportswear", "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400", "30-80% OFF", "MEN", 6, VariantTypeUtil.CLOTHING),
                cat("Sleepwear", "sleepwear", "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=400", "40-70% OFF", "WOMEN", 7, VariantTypeUtil.CLOTHING),
                cat("Innerwear", "innerwear", "https://images.unsplash.com/photo-1521577352947-9bb58764b69a?w=400", "30-60% OFF", "MEN", 8, VariantTypeUtil.CLOTHING),
                cat("Lingerie", "lingerie", "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=400", "40-70% OFF", "WOMEN", 9, VariantTypeUtil.CLOTHING),
                cat("Footwear", "footwear", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400", "30-60% OFF", "MEN", 10, VariantTypeUtil.FOOTWEAR),
                cat("Watches", "watches", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400", "20-60% OFF", "MEN", 11, VariantTypeUtil.ACCESSORY),
                cat("Grooming", "grooming", "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400", "30-50% OFF", "BEAUTY", 12, VariantTypeUtil.BEAUTY),
                cat("Beauty Products", "beauty-products", "https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400", "40-70% OFF", "BEAUTY", 13, VariantTypeUtil.BEAUTY)
        );
        categoryRepository.saveAll(categories);
    }

    private Category cat(String name, String slug, String image, String discount, String nav, int order, String variantType) {
        return Category.builder()
                .name(name).slug(slug).imageUrl(image)
                .discountText(discount).parentNav(nav).displayOrder(order)
                .variantType(variantType)
                .build();
    }

    private void ensureFootwearCategory() {
        if (categoryRepository.findBySlug("footwear").isEmpty()) {
            categoryRepository.save(cat("Footwear", "footwear",
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
                    "30-60% OFF", "MEN", 10, VariantTypeUtil.FOOTWEAR));
        }
    }

    private void backfillVariantTypes() {
        for (Category category : categoryRepository.findAll()) {
            String expected = VARIANT_BY_SLUG.getOrDefault(category.getSlug(), VariantTypeUtil.CLOTHING);
            if (category.getVariantType() == null || !expected.equals(category.getVariantType())) {
                category.setVariantType(expected);
                categoryRepository.save(category);
            }
        }
    }

    private void seedProducts() {
        Category casualWear = categoryRepository.findBySlug("casual-wear").orElseThrow();
        Category ethnicWear = categoryRepository.findBySlug("ethnic-wear").orElseThrow();
        Category westernWear = categoryRepository.findBySlug("western-wear").orElseThrow();
        Category activewear = categoryRepository.findBySlug("mens-activewear").orElseThrow();
        Category footwear = categoryRepository.findBySlug("footwear").orElseThrow();
        Category watches = categoryRepository.findBySlug("watches").orElseThrow();
        Category grooming = categoryRepository.findBySlug("grooming").orElseThrow();
        Category beauty = categoryRepository.findBySlug("beauty-products").orElseThrow();

        List<Product> products = Arrays.asList(
                // CLOTHING
                product("ONYX WEAR", "Men Casual Shirt", casualWear, "MEN",
                        new BigDecimal("499"), new BigDecimal("999"), 50,
                        "https://images.unsplash.com/photo-1596755094514-f87e34085b56?w=600",
                        Arrays.asList("38", "40", "42", "44", "46"),
                        Arrays.asList("Black", "Blue", "White"), 4.2, 128, 15),
                product("XL Wear", "Casual Shirt", casualWear, "MEN",
                        new BigDecimal("1152"), new BigDecimal("2499"), 54,
                        "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600",
                        Arrays.asList("S", "M", "L", "XL", "XXL"),
                        Arrays.asList("Blue", "Brown", "Pink"), 3.3, 12, 8),
                product("Wear Your Opinion", "Printed Casual Shirt", casualWear, "MEN",
                        new BigDecimal("899"), new BigDecimal("1999"), 55,
                        "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600",
                        Arrays.asList("S", "M", "L", "XL"),
                        Arrays.asList("Beige", "White", "Grey"), 4.0, 45, 20),
                product("Roadster", "Slim Fit Casual Shirt", casualWear, "MEN",
                        new BigDecimal("749"), new BigDecimal("1499"), 50,
                        "https://images.unsplash.com/photo-1603252109303-2751441dd157?w=600",
                        Arrays.asList("S", "M", "L", "XL", "XXL"),
                        Arrays.asList("Navy Blue", "Black", "Red"), 4.5, 89, 12),
                product("Manyavar", "Embroidered Kurta", ethnicWear, "MEN",
                        new BigDecimal("1299"), new BigDecimal("2999"), 57,
                        "https://images.unsplash.com/photo-1610037129982-bef02da415e4?w=600",
                        Arrays.asList("S", "M", "L", "XL"),
                        Arrays.asList("Pink", "Peach", "Green"), 4.6, 234, 25),
                product("Puma", "Training T-Shirt", activewear, "MEN",
                        new BigDecimal("899"), new BigDecimal("1799"), 50,
                        "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=600",
                        Arrays.asList("S", "M", "L", "XL"),
                        Arrays.asList("Navy Blue", "Grey"), 4.0, 92, 30),
                product("H&M", "Floral Print Dress", westernWear, "WOMEN",
                        new BigDecimal("1599"), new BigDecimal("2999"), 47,
                        "https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=600",
                        Arrays.asList("XS", "S", "M", "L"),
                        Arrays.asList("Pink", "Blue"), 4.1, 78, 18),
                product("Zara", "High Waist Jeans", westernWear, "WOMEN",
                        new BigDecimal("2199"), new BigDecimal("3999"), 45,
                        "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=600",
                        Arrays.asList("26", "28", "30", "32"),
                        Arrays.asList("Blue", "Black"), 4.3, 156, 22),
                product("Levis", "Classic Denim Jacket", casualWear, "MEN",
                        new BigDecimal("2499"), new BigDecimal("4999"), 50,
                        "https://images.unsplash.com/photo-1576995853123-5a10305d93b0?w=600",
                        Arrays.asList("S", "M", "L", "XL"),
                        Arrays.asList("Blue", "Black"), 4.7, 312, 10),

                // FOOTWEAR - UK sizes
                product("Nike", "Quest 6 Men's Road Running Shoes", footwear, "MEN",
                        new BigDecimal("4999"), new BigDecimal("8999"), 44,
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600",
                        Arrays.asList("6", "7", "8", "9", "10", "11"),
                        Arrays.asList("Black", "Red"), 4.4, 567, 5),
                product("Adidas", "Men Running Sports Shoes", footwear, "MEN",
                        new BigDecimal("3499"), new BigDecimal("5999"), 42,
                        "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600",
                        Arrays.asList("7", "8", "9", "10", "11"),
                        Arrays.asList("White", "Blue"), 4.3, 890, 12),
                product("Reebok", "Men Training Shoes", footwear, "MEN",
                        new BigDecimal("2799"), new BigDecimal("4499"), 38,
                        "https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=600",
                        Arrays.asList("6", "7", "8", "9", "10"),
                        Arrays.asList("Grey", "Black"), 4.1, 210, 8),

                // ACCESSORY - Free Size
                product("Fastrack", "Analog Watch for Men", watches, "MEN",
                        new BigDecimal("1299"), new BigDecimal("2499"), 48,
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                        Arrays.asList("Free Size"),
                        Arrays.asList("Black", "Silver"), 4.2, 340, 20),
                product("Titan", "Edge Ceramic Watch", watches, "MEN",
                        new BigDecimal("8999"), new BigDecimal("12999"), 31,
                        "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600",
                        Arrays.asList("Free Size"),
                        Arrays.asList("Gold", "Silver"), 4.6, 120, 7),

                // BEAUTY - volume / pack quantity
                product("Beardo", "Beard Oil", grooming, "MEN",
                        new BigDecimal("299"), new BigDecimal("499"), 40,
                        "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600",
                        Arrays.asList("30ml", "50ml", "100ml"),
                        Arrays.asList("Natural"), 4.3, 890, 50),
                product("The Man Company", "Face Wash", grooming, "MEN",
                        new BigDecimal("249"), new BigDecimal("399"), 38,
                        "https://images.unsplash.com/photo-1556228578-0d85b1a4d571?w=600",
                        Arrays.asList("50ml", "100ml", "200ml"),
                        Arrays.asList("Charcoal"), 4.1, 450, 40),
                product("Maybelline", "Matte Lipstick", beauty, "WOMEN",
                        new BigDecimal("499"), new BigDecimal("799"), 38,
                        "https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=600",
                        Arrays.asList("Pack of 1", "Pack of 2"),
                        Arrays.asList("Red", "Pink", "Nude"), 4.5, 1200, 35),
                product("Lakme", "Absolute Skin Natural", beauty, "WOMEN",
                        new BigDecimal("599"), new BigDecimal("899"), 33,
                        "https://images.unsplash.com/photo-1571781926291-c77df80a26aa?w=600",
                        Arrays.asList("15ml", "30ml", "50ml"),
                        Arrays.asList("Ivory", "Beige"), 4.2, 670, 25)
        );
        productRepository.saveAll(products);
    }

    private void ensureExtraProducts() {
        if (productRepository.count() < 12) {
            // DB already has old seed; add footwear/beauty/watch if missing brands
            addIfMissing("Adidas", "Men Running Sports Shoes", "footwear",
                    Arrays.asList("7", "8", "9", "10", "11"),
                    new BigDecimal("3499"), new BigDecimal("5999"), 42,
                    "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600");
            addIfMissing("Fastrack", "Analog Watch for Men", "watches",
                    Arrays.asList("Free Size"),
                    new BigDecimal("1299"), new BigDecimal("2499"), 48,
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600");
            addIfMissing("Beardo", "Beard Oil", "grooming",
                    Arrays.asList("30ml", "50ml", "100ml"),
                    new BigDecimal("299"), new BigDecimal("499"), 40,
                    "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600");
            addIfMissing("Maybelline", "Matte Lipstick", "beauty-products",
                    Arrays.asList("Pack of 1", "Pack of 2"),
                    new BigDecimal("499"), new BigDecimal("799"), 38,
                    "https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=600");
        }

        // Move Nike shoes from activewear to footwear if present
        productRepository.findAll().stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains("running shoes"))
                .forEach(p -> categoryRepository.findBySlug("footwear").ifPresent(fw -> {
                    p.setCategory(fw);
                    p.setSizes(Arrays.asList("6", "7", "8", "9", "10", "11"));
                    productRepository.save(p);
                }));
    }

    private void addIfMissing(String brand, String name, String categorySlug,
                              List<String> variants, BigDecimal price, BigDecimal mrp,
                              int discount, String image) {
        boolean exists = productRepository.findAll().stream()
                .anyMatch(p -> brand.equalsIgnoreCase(p.getBrand()) && name.equalsIgnoreCase(p.getName()));
        if (exists) return;

        categoryRepository.findBySlug(categorySlug).ifPresent(cat ->
                productRepository.save(product(brand, name, cat,
                        "BEAUTY".equals(cat.getParentNav()) ? "WOMEN" : "MEN",
                        price, mrp, discount, image, variants,
                        Arrays.asList("Default"), 4.2, 100, 20)));
    }

    private Product product(String brand, String name, Category category, String gender,
                            BigDecimal price, BigDecimal mrp, int discount, String image,
                            List<String> sizes, List<String> colors,
                            double rating, int ratingCount, int stock) {
        return Product.builder()
                .brand(brand).name(name).category(category).genderSection(gender)
                .price(price).mrp(mrp).discountPercent(discount)
                .imageUrl(image).images(Arrays.asList(image, image))
                .sizes(sizes).colors(colors)
                .rating(rating).ratingCount(ratingCount).stockQuantity(stock)
                .description("Premium quality " + name.toLowerCase() + " from " + brand)
                .build();
    }

    private void seedNavMenus() {
        seedWomenNav();
        seedMenNav();
        seedKidsNav();
        seedHomeNav();
        seedBeautyNav();
        seedGenzNav();
        seedStudioNav();
    }

    private void ensureNavMenus() {
        // Backfill missing linkSlug on existing rows
        for (NavMenuItem item : navMenuItemRepository.findAll()) {
            if (item.getLinkSlug() == null || item.getLinkSlug().isBlank()) {
                item.setLinkSlug(guessSlugFromName(item.getItemName()));
                item.setLinkType("CATEGORY");
                navMenuItemRepository.save(item);
            }
        }

        if (navMenuItemRepository.findByNavSectionOrderByColumnIndexAscDisplayOrderAsc("KIDS").isEmpty()) {
            seedKidsNav();
        }
        if (navMenuItemRepository.findByNavSectionOrderByColumnIndexAscDisplayOrderAsc("HOME").isEmpty()) {
            seedHomeNav();
        }
        if (navMenuItemRepository.findByNavSectionOrderByColumnIndexAscDisplayOrderAsc("BEAUTY").isEmpty()) {
            seedBeautyNav();
        }
        if (navMenuItemRepository.findByNavSectionOrderByColumnIndexAscDisplayOrderAsc("GENZ").isEmpty()) {
            seedGenzNav();
        }
        if (navMenuItemRepository.findByNavSectionOrderByColumnIndexAscDisplayOrderAsc("STUDIO").isEmpty()) {
            seedStudioNav();
        }
    }

    private void seedWomenNav() {
        saveNav("WOMEN", 1, "Indian & Fusion Wear", "Kurtas & Suits", "ethnic-wear", 1);
        saveNav("WOMEN", 1, "Indian & Fusion Wear", "Sarees", "ethnic-wear", 2);
        saveNav("WOMEN", 1, "Indian & Fusion Wear", "Ethnic Wear", "ethnic-wear", 3);
        saveNav("WOMEN", 1, "Indian & Fusion Wear", "Lehenga Cholis", "ethnic-wear", 4);
        saveNav("WOMEN", 2, "Western Wear", "Dresses", "western-wear", 1);
        saveNav("WOMEN", 2, "Western Wear", "Tops", "western-wear", 2);
        saveNav("WOMEN", 2, "Western Wear", "Tshirts", "western-wear", 3);
        saveNav("WOMEN", 2, "Western Wear", "Jeans", "western-wear", 4);
        saveNav("WOMEN", 2, "Western Wear", "Jumpsuits", "western-wear", 5);
        saveNav("WOMEN", 3, "Footwear", "Flats", "footwear", 1);
        saveNav("WOMEN", 3, "Footwear", "Heels", "footwear", 2);
        saveNav("WOMEN", 3, "Footwear", "Sports Shoes", "footwear", 3);
        saveNav("WOMEN", 3, "Sports & Active Wear", "Clothing", "womens-activewear", 4);
        saveNav("WOMEN", 4, "Lingerie & Sleepwear", "Bra", "lingerie", 1);
        saveNav("WOMEN", 4, "Lingerie & Sleepwear", "Sleepwear", "sleepwear", 2);
        saveNav("WOMEN", 4, "Beauty & Personal Care", "Makeup", "beauty-products", 3);
        saveNav("WOMEN", 4, "Beauty & Personal Care", "Skincare", "beauty-products", 4);
        saveNav("WOMEN", 5, "Accessories", "Watches", "watches", 1);
        saveNav("WOMEN", 5, "Accessories", "Handbags", "western-wear", 2);
    }

    private void seedMenNav() {
        saveNav("MEN", 1, "Topwear", "T-Shirts", "casual-wear", 1);
        saveNav("MEN", 1, "Topwear", "Casual Shirts", "casual-wear", 2);
        saveNav("MEN", 1, "Topwear", "Formal Shirts", "casual-wear", 3);
        saveNav("MEN", 1, "Topwear", "Jackets", "casual-wear", 4);
        saveNav("MEN", 1, "Indian & Festive Wear", "Kurtas & Kurta Sets", "ethnic-wear", 5);
        saveNav("MEN", 2, "Bottomwear", "Jeans", "casual-wear", 1);
        saveNav("MEN", 2, "Bottomwear", "Casual Trousers", "casual-wear", 2);
        saveNav("MEN", 2, "Bottomwear", "Shorts", "casual-wear", 3);
        saveNav("MEN", 3, "Footwear", "Casual Shoes", "footwear", 1);
        saveNav("MEN", 3, "Footwear", "Sports Shoes", "footwear", 2);
        saveNav("MEN", 3, "Footwear", "Formal Shoes", "footwear", 3);
        saveNav("MEN", 3, "Innerwear & Sleepwear", "Briefs & Trunks", "innerwear", 4);
        saveNav("MEN", 4, "Personal Care & Grooming", "Face Wash", "grooming", 1);
        saveNav("MEN", 4, "Personal Care & Grooming", "Beard Care", "grooming", 2);
        saveNav("MEN", 4, "Fashion Accessories", "Watches", "watches", 3);
        saveNav("MEN", 5, "Sports & Active Wear", "Active T-Shirts", "mens-activewear", 1);
        saveNav("MEN", 5, "Sports & Active Wear", "Tracksuits", "sportswear", 2);
        saveNav("MEN", 5, "Sports & Active Wear", "Sports Shoes", "footwear", 3);
    }

    private void seedKidsNav() {
        saveNav("KIDS", 1, "Boys Clothing", "T-Shirts", "casual-wear", 1);
        saveNav("KIDS", 1, "Boys Clothing", "Shirts", "casual-wear", 2);
        saveNav("KIDS", 1, "Boys Clothing", "Shorts", "casual-wear", 3);
        saveNav("KIDS", 2, "Girls Clothing", "Dresses", "western-wear", 1);
        saveNav("KIDS", 2, "Girls Clothing", "Tops", "western-wear", 2);
        saveNav("KIDS", 3, "Footwear", "Sports Shoes", "footwear", 1);
        saveNav("KIDS", 3, "Footwear", "Casual Shoes", "footwear", 2);
        saveNav("KIDS", 4, "Infants", "Rompers", "casual-wear", 1);
        saveNav("KIDS", 5, "Kids Accessories", "Watches", "watches", 1);
    }

    private void seedHomeNav() {
        saveNav("HOME", 1, "Bed Linen", "Bedsheets", "sleepwear", 1);
        saveNav("HOME", 1, "Bed Linen", "Blankets", "sleepwear", 2);
        saveNav("HOME", 2, "Loungewear", "Sleepwear", "sleepwear", 1);
        saveNav("HOME", 3, "Comfort", "Innerwear", "innerwear", 1);
        saveNav("HOME", 4, "Lifestyle", "Watches", "watches", 1);
        saveNav("HOME", 5, "Essentials", "Casual Wear", "casual-wear", 1);
    }

    private void seedBeautyNav() {
        saveNav("BEAUTY", 1, "Makeup", "Lipstick", "beauty-products", 1);
        saveNav("BEAUTY", 1, "Makeup", "Foundation", "beauty-products", 2);
        saveNav("BEAUTY", 1, "Makeup", "Kajal", "beauty-products", 3);
        saveNav("BEAUTY", 2, "Skincare", "Face Wash", "beauty-products", 1);
        saveNav("BEAUTY", 2, "Skincare", "Moisturiser", "beauty-products", 2);
        saveNav("BEAUTY", 3, "Haircare", "Shampoo", "beauty-products", 1);
        saveNav("BEAUTY", 4, "Fragrance", "Perfume", "beauty-products", 1);
        saveNav("BEAUTY", 5, "Men's Grooming", "Beard Care", "grooming", 1);
        saveNav("BEAUTY", 5, "Men's Grooming", "Face Wash", "grooming", 2);
    }

    private void seedGenzNav() {
        saveNav("GENZ", 1, "Trends", "Oversized Tees", "casual-wear", 1);
        saveNav("GENZ", 1, "Trends", "Cargo Pants", "casual-wear", 2);
        saveNav("GENZ", 2, "Streetwear", "Hoodies", "sportswear", 1);
        saveNav("GENZ", 2, "Streetwear", "Sneakers", "footwear", 2);
        saveNav("GENZ", 3, "Beauty", "Lip Tint", "beauty-products", 1);
        saveNav("GENZ", 4, "Accessories", "Watches", "watches", 1);
        saveNav("GENZ", 5, "Active", "Athleisure", "mens-activewear", 1);
    }

    private void seedStudioNav() {
        saveNav("STUDIO", 1, "Collections", "New Arrivals", "casual-wear", 1);
        saveNav("STUDIO", 1, "Collections", "Editor's Picks", "western-wear", 2);
        saveNav("STUDIO", 2, "Looks", "Festive Looks", "ethnic-wear", 1);
        saveNav("STUDIO", 2, "Looks", "Workwear", "casual-wear", 2);
        saveNav("STUDIO", 3, "Guides", "Style Tips", "casual-wear", 1);
    }

    private void saveNav(String section, int col, String group, String item, String slug, int order) {
        navMenuItemRepository.save(NavMenuItem.builder()
                .navSection(section).columnIndex(col)
                .groupTitle(group).itemName(item)
                .linkSlug(slug).linkType("CATEGORY")
                .displayOrder(order)
                .build());
    }

    private String guessSlugFromName(String itemName) {
        if (itemName == null) return "casual-wear";
        String n = itemName.toLowerCase();
        if (n.contains("shoe") || n.contains("footwear") || n.contains("sneaker") || n.contains("heel") || n.contains("flat")) return "footwear";
        if (n.contains("watch")) return "watches";
        if (n.contains("kurta") || n.contains("ethnic") || n.contains("saree") || n.contains("lehenga")) return "ethnic-wear";
        if (n.contains("dress") || n.contains("top") || n.contains("jean") || n.contains("western") || n.contains("jumpsuit")) return "western-wear";
        if (n.contains("makeup") || n.contains("skin") || n.contains("lipstick") || n.contains("beauty") || n.contains("perfume") || n.contains("shampoo")) return "beauty-products";
        if (n.contains("beard") || n.contains("face wash") || n.contains("groom")) return "grooming";
        if (n.contains("active") || n.contains("athleisure")) return "mens-activewear";
        if (n.contains("sport") || n.contains("track") || n.contains("hoodie")) return "sportswear";
        if (n.contains("sleep") || n.contains("lounge") || n.contains("bedsheet") || n.contains("blanket")) return "sleepwear";
        if (n.contains("inner") || n.contains("brief") || n.contains("vest")) return "innerwear";
        if (n.contains("lingerie") || n.contains("bra")) return "lingerie";
        return "casual-wear";
    }
}
