package com.ecommerce.service;

import com.ecommerce.dto.DeliveryCheckDto;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class DeliveryService {

    private static final Set<String> SERVICEABLE_PINCODES = Set.of(
            "201301", "201309", "201310", "110001", "110002", "400001",
            "560001", "560034", "500001", "700001", "302001", "380001"
    );

    private static final Map<String, String> PINCODE_CITIES = Map.ofEntries(
            Map.entry("201301", "Noida"),
            Map.entry("201309", "Noida"),
            Map.entry("201310", "Noida"),
            Map.entry("110001", "New Delhi"),
            Map.entry("110002", "New Delhi"),
            Map.entry("400001", "Mumbai"),
            Map.entry("560001", "Bengaluru"),
            Map.entry("560034", "Bengaluru"),
            Map.entry("500001", "Hyderabad"),
            Map.entry("700001", "Kolkata"),
            Map.entry("302001", "Jaipur"),
            Map.entry("380001", "Ahmedabad")
    );

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMM dd", Locale.ENGLISH);

    public DeliveryCheckDto checkDelivery(String pincode) {
        if (pincode == null || !pincode.matches("^[0-9]{6}$")) {
            return DeliveryCheckDto.builder()
                    .pincode(pincode)
                    .deliverable(false)
                    .message("Please enter a valid 6-digit pincode")
                    .build();
        }

        boolean deliverable = SERVICEABLE_PINCODES.contains(pincode);

        if (!deliverable) {
            return DeliveryCheckDto.builder()
                    .pincode(pincode)
                    .deliverable(false)
                    .message("Sorry, we do not deliver to this pincode yet")
                    .build();
        }

        int deliveryDays = 3 + (Math.abs(pincode.hashCode()) % 5);
        LocalDate deliveryDate = addBusinessDays(LocalDate.now(), deliveryDays);

        return DeliveryCheckDto.builder()
                .pincode(pincode)
                .deliverable(true)
                .estimatedDeliveryDate(deliveryDate.format(DISPLAY_FORMAT))
                .codAvailable(true)
                .returnDays(10)
                .city(PINCODE_CITIES.getOrDefault(pincode, "Your City"))
                .message("Delivery available to this pincode")
                .build();
    }

    public String getEstimatedDeliveryDate(String pincode) {
        DeliveryCheckDto check = checkDelivery(pincode);
        return check.isDeliverable() ? check.getEstimatedDeliveryDate() : null;
    }

    private LocalDate addBusinessDays(LocalDate start, int days) {
        LocalDate result = start;
        int added = 0;
        while (added < days) {
            result = result.plusDays(1);
            if (result.getDayOfWeek() != DayOfWeek.SUNDAY) {
                added++;
            }
        }
        return result;
    }
}
