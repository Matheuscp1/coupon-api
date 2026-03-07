package org.coupon.api.service;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.coupon.api.domain.Coupon;
import org.coupon.api.dto.CouponRequestDTO;
import org.coupon.api.dto.CouponResponseDTO;
import org.coupon.api.exception.BusinessException;
import org.coupon.api.repository.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CouponService {

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository repository;

    public CouponService(CouponRepository repository) {
        this.repository = repository;
    }

    public CouponResponseDTO create(CouponRequestDTO request) {
        log.info("Creating coupon with code: {}", request.code());
        try {
            Coupon coupon = Coupon.create(
                    request.code(),
                    request.description(),
                    request.discountValue(),
                    request.expirationDate(),
                    request.published()
            );

            repository.save(coupon);
            log.info("Coupon saved successfully: {}", coupon.getCode());

            return new CouponResponseDTO(
                    coupon.getId(),
                    coupon.getCode(),
                    coupon.getDescription(),
                    coupon.getDiscountValue(),
                    coupon.getExpirationDate(),
                    coupon.isPublished()
            );

        } catch (BusinessException e) {
            log.error("Failed to create coupon: {} - {}", request.code(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating coupon: {}", e.getMessage(), e);
            throw new RuntimeException("Internal error during coupon creation");
        }
    }

    public void delete(UUID id) {
        log.info("Deleting coupon with id: {}", id);

        try {
            Coupon coupon = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Coupon with id {} not found", id);
                        return new BusinessException("Coupon not found");
                    });

            coupon.delete();
            repository.save(coupon);
            log.info("Coupon soft-deleted successfully: {}", coupon.getCode());

        } catch (BusinessException e) {
            log.error("Failed to delete coupon id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting coupon id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Internal error during coupon deletion");
        }
    }
}
