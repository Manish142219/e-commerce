package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.entity.*;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressService addressService;
    private final CartService cartService;

    public List<OrderDto> getOrders(String email) {
        User user = getUser(email);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrder(String email, Long orderId) {
        User user = getUser(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return toDto(order);
    }

    @Transactional
    public OrderDto placeOrder(String email, PlaceOrderRequest request) {
        User user = getUser(email);
        AddressDto address = addressService.getAddressById(email, request.getAddressId());

        CartSummaryDto cart = cartService.getCart(email, address.getPincode());
        List<CartItemDto> selected = cart.getItems().stream()
                .filter(CartItemDto::isSelected)
                .collect(Collectors.toList());

        if (selected.isEmpty()) {
            throw new RuntimeException("No items selected to place order");
        }

        Order order = Order.builder()
                .user(user)
                .status("PLACED")
                .totalAmount(cart.getTotalAmount())
                .deliveryName(address.getName())
                .deliveryPhone(address.getPhone())
                .deliveryAddress(address.getAddressLine() + ", " + address.getCity() + ", " + address.getState())
                .deliveryPincode(address.getPincode())
                .build();

        for (CartItemDto item : selected) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(item.getProductId())
                    .brand(item.getBrand())
                    .name(item.getName())
                    .imageUrl(item.getImageUrl())
                    .size(item.getSize())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .mrp(item.getMrp())
                    .build();
            order.getItems().add(orderItem);
        }

        order = orderRepository.save(order);

        // Remove ordered items from cart
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        for (CartItemDto selectedItem : selected) {
            cartItems.stream()
                    .filter(ci -> ci.getId().equals(selectedItem.getId()))
                    .findFirst()
                    .ifPresent(cartItemRepository::delete);
        }

        return toDto(order);
    }

    private OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryName(order.getDeliveryName())
                .deliveryPhone(order.getDeliveryPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryPincode(order.getDeliveryPincode())
                .createdAt(order.getCreatedAt() != null
                        ? order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
                        : null)
                .items(order.getItems().stream().map(i -> OrderItemDto.builder()
                        .productId(i.getProductId())
                        .brand(i.getBrand())
                        .name(i.getName())
                        .imageUrl(i.getImageUrl())
                        .size(i.getSize())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .mrp(i.getMrp())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
