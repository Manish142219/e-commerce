package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.entity.UserCartPreference;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserCartPreferenceRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserCartPreferenceRepository preferenceRepository;
    private final DeliveryService deliveryService;
    private final AddressService addressService;

    private static final BigDecimal PLATFORM_FEE = new BigDecimal("23.00");

    private static final Map<String, BigDecimal> COUPONS = Map.of(
            "MYNTRA300", new BigDecimal("300.00"),
            "SAVE500", new BigDecimal("500.00"),
            "FLAT10", new BigDecimal("100.00")
    );

    public CartSummaryDto getCart(String email, String pincode) {
        User user = getUser(email);
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());
        UserCartPreference pref = getOrCreatePreference(user);

        String effectivePincode = pincode != null && !pincode.isBlank()
                ? pincode
                : pref.getSelectedPincode();

        if (effectivePincode != null && !effectivePincode.isBlank()) {
            pref.setSelectedPincode(effectivePincode);
            preferenceRepository.save(pref);
        }

        AddressDto deliveryAddress = addressService.getDefaultAddress(email);
        return buildSummary(items, pref, effectivePincode, deliveryAddress);
    }

    @Transactional
    public CartItemDto addToCart(String email, AddToCartRequest request) {
        User user = getUser(email);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository
                .findByUserIdAndProductIdAndSize(user.getId(), product.getId(), request.getSize())
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .size(request.getSize())
                    .quantity(request.getQuantity())
                    .selected(true)
                    .build();
        }

        cartItem = cartItemRepository.save(cartItem);
        return toDto(cartItem, null);
    }

    @Transactional
    public void updateQuantity(String email, Long cartItemId, Integer quantity) {
        User user = getUser(email);
        CartItem item = getOwnedItem(user, cartItemId);
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Transactional
    public void toggleSelection(String email, Long cartItemId, boolean selected) {
        User user = getUser(email);
        CartItem item = getOwnedItem(user, cartItemId);
        item.setSelected(selected);
        cartItemRepository.save(item);
    }

    @Transactional
    public void toggleAllSelection(String email, boolean selected) {
        User user = getUser(email);
        cartItemRepository.findByUserId(user.getId()).forEach(item -> {
            item.setSelected(selected);
            cartItemRepository.save(item);
        });
    }

    @Transactional
    public void removeFromCart(String email, Long cartItemId) {
        User user = getUser(email);
        CartItem item = getOwnedItem(user, cartItemId);
        cartItemRepository.delete(item);
    }

    @Transactional
    public CartSummaryDto applyCoupon(String email, String couponCode) {
        User user = getUser(email);
        UserCartPreference pref = getOrCreatePreference(user);

        String code = couponCode.toUpperCase().trim();
        if (!COUPONS.containsKey(code)) {
            throw new RuntimeException("Invalid coupon code");
        }

        pref.setAppliedCoupon(code);
        preferenceRepository.save(pref);

        return getCart(email, pref.getSelectedPincode());
    }

    @Transactional
    public CartSummaryDto removeCoupon(String email) {
        User user = getUser(email);
        UserCartPreference pref = getOrCreatePreference(user);
        pref.setAppliedCoupon(null);
        preferenceRepository.save(pref);
        return getCart(email, pref.getSelectedPincode());
    }

    public int getCartCount(String email) {
        User user = getUser(email);
        return cartItemRepository.countByUserId(user.getId());
    }

    private CartSummaryDto buildSummary(List<CartItem> items, UserCartPreference pref,
                                        String pincode, AddressDto deliveryAddress) {
        String deliveryDate = pincode != null ? deliveryService.getEstimatedDeliveryDate(pincode) : null;

        List<CartItemDto> dtos = items.stream()
                .map(item -> toDto(item, deliveryDate))
                .collect(Collectors.toList());

        BigDecimal totalMrp = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        int selectedCount = 0;

        for (CartItemDto item : dtos) {
            if (item.isSelected()) {
                selectedCount++;
                totalMrp = totalMrp.add(item.getMrp().multiply(BigDecimal.valueOf(item.getQuantity())));
                totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        BigDecimal discount = totalMrp.subtract(totalPrice);
        BigDecimal couponDiscount = BigDecimal.ZERO;

        if (pref.getAppliedCoupon() != null && COUPONS.containsKey(pref.getAppliedCoupon()) && selectedCount > 0) {
            couponDiscount = COUPONS.get(pref.getAppliedCoupon());
        }

        BigDecimal total = selectedCount > 0
                ? totalPrice.subtract(couponDiscount).add(PLATFORM_FEE)
                : BigDecimal.ZERO;

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        return CartSummaryDto.builder()
                .items(dtos)
                .totalMrp(totalMrp)
                .totalDiscount(discount)
                .couponDiscount(couponDiscount)
                .platformFee(selectedCount > 0 ? PLATFORM_FEE : BigDecimal.ZERO)
                .totalAmount(total)
                .itemCount(dtos.size())
                .selectedItemCount(selectedCount)
                .appliedCouponCode(pref.getAppliedCoupon())
                .deliveryAddress(deliveryAddress)
                .build();
    }

    private CartItemDto toDto(CartItem item, String deliveryDate) {
        Product p = item.getProduct();
        return CartItemDto.builder()
                .id(item.getId())
                .productId(p.getId())
                .brand(p.getBrand())
                .name(p.getName())
                .imageUrl(p.getImageUrl())
                .size(item.getSize())
                .quantity(item.getQuantity())
                .price(p.getPrice())
                .mrp(p.getMrp())
                .discountPercent(p.getDiscountPercent())
                .stockQuantity(p.getStockQuantity())
                .selected(item.isSelected())
                .estimatedDeliveryDate(deliveryDate)
                .build();
    }

    private CartItem getOwnedItem(User user, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return item;
    }

    private UserCartPreference getOrCreatePreference(User user) {
        return preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> preferenceRepository.save(
                        UserCartPreference.builder().user(user).build()));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
