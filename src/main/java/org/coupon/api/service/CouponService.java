package org.coupon.api.service;

import org.coupon.api.domain.Coupon;
import org.coupon.api.dto.CouponRequestDTO;
import org.coupon.api.dto.CouponResponseDTO;
import org.coupon.api.exception.BusinessException;
import org.coupon.api.repository.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
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

    public List<CouponResponseDTO> findAll() {
        log.info("Fetching all coupons");

        try {
            List<Coupon> coupons = repository.findAll();

            log.info("Total coupons found: {}", coupons.size());

            return coupons.stream()
                    .map(this::toDTO)
                    .toList();

        } catch (Exception e) {
            log.error("Unexpected error while fetching coupons: {}", e.getMessage(), e);
            throw new RuntimeException("Internal error during coupons fetch");
        }
    }

    public CouponResponseDTO findById(UUID id) {
        log.info("Fetching coupon with id: {}", id);

        try {
            Coupon coupon = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Coupon with id {} not found", id);
                        return new BusinessException("Coupon not found");
                    });

            log.info("Coupon found: {}", coupon.getCode());

            return toDTO(coupon);

        } catch (BusinessException e) {
            log.error("Failed to fetch coupon id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching coupon id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Internal error during coupon fetch");
        }
    }

    public CouponResponseDTO update(UUID id, CouponRequestDTO request) {
        log.info("Updating coupon with id: {}", id);

        try {
            Coupon coupon = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Coupon with id {} not found", id);
                        return new BusinessException("Coupon not found");
                    });

            Coupon couponUpdate = Coupon.create(
                    request.code(),
                    request.description(),
                    request.discountValue(),
                    request.expirationDate(),
                    request.published()
            );
            couponUpdate.setId(id);
            repository.save(couponUpdate);
            log.info("Coupon updated successfully: {}", couponUpdate.getCode());
            return toDTO(couponUpdate);

        } catch (BusinessException e) {
            log.error("Failed to update coupon id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating coupon id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Internal error during coupon update");
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

    private CouponResponseDTO toDTO(Coupon coupon) {
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.isPublished()
        );
    }
}
