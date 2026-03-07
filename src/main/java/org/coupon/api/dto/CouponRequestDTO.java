package org.coupon.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CouponRequestDTO(
        String code,
        String description,
        BigDecimal discountValue,
        LocalDate expirationDate,
        boolean published
) {}