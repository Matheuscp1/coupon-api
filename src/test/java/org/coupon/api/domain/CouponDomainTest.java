package org.coupon.api.domain;

import org.coupon.api.exception.BusinessException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CouponDomainTest {

    @Test
    void shouldSanitizeCodeAndHaveExactly6Chars() {
        Coupon coupon = Coupon.create(
                "ABC123",
                "Test coupon",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );

        assertEquals("ABC123", coupon.getCode().substring(0, 6));
        assertEquals(6, coupon.getCode().length());
    }

    @Test
    void shouldThrowExceptionWhenCodeAfterSanitizeHasWrongLength() {
        Exception exception = assertThrows(BusinessException.class, () ->
                Coupon.create(
                        "ABC@!", // menos de 6 caracteres
                        "Test",
                        BigDecimal.valueOf(1),
                        LocalDate.now().plusDays(1),
                        true
                )
        );

        assertEquals("Coupon code must have 6 characters after sanitization", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForDiscountBelowMinimum() {
        Exception exception = assertThrows(BusinessException.class, () ->
                Coupon.create(
                        "ABC123",
                        "Test",
                        BigDecimal.valueOf(0.1),
                        LocalDate.now().plusDays(1),
                        true
                )
        );

        assertEquals("Discount must be at least 0.5", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForExpirationDateInPast() {
        Exception exception = assertThrows(BusinessException.class, () ->
                Coupon.create(
                        "ABC123",
                        "Test",
                        BigDecimal.valueOf(1),
                        LocalDate.now().minusDays(1),
                        true
                )
        );

        assertEquals("Expiration date cannot be in the past", exception.getMessage());
    }

    @Test
    void shouldSoftDeleteCoupon() {
        Coupon coupon = Coupon.create(
                "ABC123",
                "Test",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );

        coupon.delete();
        assertTrue(coupon.isDeleted());
    }

    @Test
    void shouldNotDeleteAlreadyDeletedCoupon() {
        Coupon coupon = Coupon.create(
                "ABC123",
                "Test",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );

        coupon.delete();

        Exception exception = assertThrows(BusinessException.class, coupon::delete);
        assertEquals("Coupon already deleted", exception.getMessage());
    }

    @Test
    void shouldCreateValidCoupon() {
        Coupon coupon = Coupon.create(
                "ABC123",
                "Test",
                BigDecimal.valueOf(1),
                LocalDate.now().plusDays(1),
                true
        );

        assertEquals("ABC123", coupon.getCode().substring(0,6));
        assertEquals("Test", coupon.getDescription());
        assertEquals(BigDecimal.valueOf(1), coupon.getDiscountValue());
        assertEquals(LocalDate.now().plusDays(1), coupon.getExpirationDate());
        assertTrue(coupon.isPublished());
        assertFalse(coupon.isDeleted());
    }
}