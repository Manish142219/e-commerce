package com.ecommerce.service;

import com.ecommerce.dto.AddressDto;
import com.ecommerce.dto.CreateAddressRequest;
import com.ecommerce.entity.Address;
import com.ecommerce.entity.User;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<AddressDto> getAddresses(String email) {
        User user = getUser(email);
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AddressDto getDefaultAddress(String email) {
        User user = getUser(email);
        return addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                .map(this::toDto)
                .orElse(null);
    }

    public AddressDto getAddressById(String email, Long addressId) {
        User user = getUser(email);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return toDto(address);
    }

    @Transactional
    public AddressDto createAddress(String email, CreateAddressRequest request) {
        User user = getUser(email);

        if (request.isDefault() || addressRepository.findByUserId(user.getId()).isEmpty()) {
            addressRepository.findByUserId(user.getId()).forEach(a -> {
                a.setDefault(false);
                addressRepository.save(a);
            });
            request.setDefault(true);
        }

        Address address = Address.builder()
                .user(user)
                .name(request.getName())
                .phone(request.getPhone())
                .pincode(request.getPincode())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .label(request.getLabel() != null ? request.getLabel() : "HOME")
                .isDefault(request.isDefault())
                .build();

        return toDto(addressRepository.save(address));
    }

    @Transactional
    public AddressDto setDefaultAddress(String email, Long addressId) {
        User user = getUser(email);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        addressRepository.findByUserId(user.getId()).forEach(a -> {
            a.setDefault(false);
            addressRepository.save(a);
        });

        address.setDefault(true);
        return toDto(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(String email, Long addressId) {
        User user = getUser(email);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        addressRepository.delete(address);
    }

    private AddressDto toDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .name(address.getName())
                .phone(address.getPhone())
                .pincode(address.getPincode())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .state(address.getState())
                .label(address.getLabel())
                .isDefault(address.isDefault())
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
