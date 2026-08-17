package com.ecommerce.repository;

import com.ecommerce.entity.UserCartPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCartPreferenceRepository extends JpaRepository<UserCartPreference, Long> {
    Optional<UserCartPreference> findByUserId(Long userId);
}
