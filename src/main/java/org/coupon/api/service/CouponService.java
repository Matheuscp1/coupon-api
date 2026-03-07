package org.coupon.api.service;

import org.coupon.api.domain.Coupon;
import org.coupon.api.dto.CouponRequestDTO;
import org.coupon.api.dto.CouponResponseDTO;
import org.coupon.api.exception.BusinessException;
import org.coupon.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CouponService {

    private final CouponRepository repository;

    public CouponService(CouponRepository repository) {
        this.repository = repository;
    }

    public CouponResponseDTO create(CouponRequestDTO request) {

        Coupon coupon = Coupon.create(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published()
        );

        repository.save(coupon);

        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.isPublished()
        );
    }

    public void delete(UUID id){

        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Coupon not found"));

        coupon.delete();

        repository.save(coupon);
    }
}
