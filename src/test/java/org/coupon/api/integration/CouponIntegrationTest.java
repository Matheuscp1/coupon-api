package org.coupon.api.integration;

import org.coupon.api.domain.Coupon;
import org.coupon.api.dto.CouponRequestDTO;
import org.coupon.api.exception.BusinessException;
import org.coupon.api.repository.CouponRepository;
import org.coupon.api.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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
                "A@B1",
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

    @Test
    void shouldFindCouponById() {
        Coupon coupon = Coupon.create(
                "GHI789",
                "Promo FindById",
                BigDecimal.valueOf(15),
                LocalDate.now().plusDays(3),
                true
        );
        repository.save(coupon);

        Coupon found = repository.findById(coupon.getId()).orElseThrow();
        assertEquals("GHI789", found.getCode());
        assertEquals("Promo FindById", found.getDescription());
    }

    @Test
    void shouldFailFindCouponByNonExistingId() {
        UUID randomId = UUID.randomUUID();
        assertTrue(repository.findById(randomId).isEmpty());
    }

    @Test
    void shouldUpdateCouponSuccessfully() {
        Coupon coupon = Coupon.create(
                "JKL012",
                "Old Promo",
                BigDecimal.valueOf(20),
                LocalDate.now().plusDays(5),
                true
        );
        repository.save(coupon);
        coupon.setDescription("Updated Promo");
        coupon.setDiscountValue(BigDecimal.valueOf(25));
        repository.save(coupon);
        Coupon updated = repository.findById(coupon.getId()).orElseThrow();
        assertEquals("Updated Promo", updated.getDescription());
        assertEquals(BigDecimal.valueOf(25), updated.getDiscountValue());
    }

    @Test
    void shouldFailUpdateNonExistingCoupon() {
        UUID nonExistingId = UUID.randomUUID();
        assertTrue(repository.findById(nonExistingId).isEmpty());
        CouponRequestDTO updateRequest = new CouponRequestDTO(
                "XYZ999",
                "Non Existing",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );
        CouponService service = new CouponService(repository);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.update(nonExistingId, updateRequest));

        assertEquals("Coupon not found", exception.getMessage());
    }
}