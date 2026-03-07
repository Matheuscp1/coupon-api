package org.coupon.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CouponRequestDTO(
        @NotNull(message = "Code is required")
        @Size(min = 6, max = 6, message = "Code must be exactly 6 characters")
        String code,

        @NotNull(message = "Description is required")
        @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
        String description,

        @NotNull(message = "Discount value is required")
        @DecimalMin(value = "0.5", message = "Discount must be at least 0.5")
        BigDecimal discountValue,

        @NotNull(message = "Expiration date is required")
        LocalDate expirationDate,

        boolean published
) {}