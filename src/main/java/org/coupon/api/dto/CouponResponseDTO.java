package org.coupon.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CouponResponseDTO(
        UUID id,
        String code,
        String description,
        BigDecimal discountValue,
        LocalDate expirationDate,
        boolean published
) {}