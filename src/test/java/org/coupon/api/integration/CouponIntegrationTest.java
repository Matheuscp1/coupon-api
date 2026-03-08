package org.coupon.api.integration;

import org.coupon.api.domain.Coupon;
import org.coupon.api.dto.CouponRequestDTO;
import org.coupon.api.exception.BusinessException;
import org.coupon.api.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CouponIntegrationTest {

    @Autowired
    private CouponRepository repository;

    @Test
    void shouldCreateCouponSuccessfully() {
        CouponRequestDTO request = new CouponRequestDTO(
                "ABC123",
                "10% OFF",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );

        Coupon coupon = Coupon.create(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published()
        );

        repository.save(coupon);

        assertNotNull(coupon.getId());
        assertEquals("ABC123", coupon.getCode());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldFailCreationWhenCodeInvalid() {
        CouponRequestDTO request = new CouponRequestDTO(
                "A@B1",  // código inválido
                "Promo",
                BigDecimal.valueOf(5),
                LocalDate.now().plusDays(1),
                true
        );

        BusinessException exception = assertThrows(BusinessException.class, () ->
                Coupon.create(
                        request.code(),
                        request.description(),
                        request.discountValue(),
                        request.expirationDate(),
                        request.published()
                )
        );

        assertEquals("Coupon code must have 6 characters after sanitization", exception.getMessage());
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void shouldSoftDeleteCoupon() {
        Coupon coupon = Coupon.create(
                "ABC123",
                "Promo",
                BigDecimal.valueOf(5),
                LocalDate.now().plusDays(1),
                true
        );
        repository.save(coupon);

        coupon.delete();
        repository.save(coupon);

        Coupon deleted = repository.findById(coupon.getId()).orElseThrow();
        assertTrue(deleted.isDeleted());
    }

    @Test
    void shouldNotDeleteAlreadyDeletedCoupon() {
        Coupon coupon = Coupon.create(
                "ABC123",
                "Promo",
                BigDecimal.valueOf(5),
                LocalDate.now().plusDays(1),
                true
        );
        repository.save(coupon);

        coupon.delete();
        repository.save(coupon);

        BusinessException exception = assertThrows(BusinessException.class, coupon::delete);
        assertEquals("Coupon already deleted", exception.getMessage());
    }

    @Test
    void shouldRetrieveAllCouponsExcludingDeleted() {
        Coupon coupon1 = Coupon.create(
                "ABC123",
                "Promo1",
                BigDecimal.valueOf(5),
                LocalDate.now().plusDays(1),
                true
        );
        Coupon coupon2 = Coupon.create(
                "DEF456",
                "Promo2",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(2),
                true
        );
        repository.saveAll(List.of(coupon1, coupon2));

        coupon1.delete();
        repository.save(coupon1);

        List<Coupon> activeCoupons = repository.findAll();
        assertEquals(1, activeCoupons.size());
        assertEquals("DEF456", activeCoupons.get(0).getCode());
    }
}