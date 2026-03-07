package org.coupon.api.service;

import org.coupon.api.domain.Coupon;
import org.coupon.api.dto.CouponRequestDTO;
import org.coupon.api.dto.CouponResponseDTO;
import org.coupon.api.exception.BusinessException;
import org.coupon.api.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    private CouponRepository repository;
    private CouponService service;

    @BeforeEach
    void setup() {
        repository = mock(CouponRepository.class);
        service = new CouponService(repository);
    }

    @Test
    void shouldCreateCouponSuccessfully() {
        CouponRequestDTO request = new CouponRequestDTO(
                "ABC123",
                "Test coupon",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );

        CouponResponseDTO response = service.create(request);

        assertEquals("ABC123", response.code());
        assertEquals("Test coupon", response.description());
        verify(repository, times(1)).save(any(Coupon.class));
    }

    @Test
    void shouldDeleteCouponSuccessfully() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.create(
                "ABC123",
                "Test",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );

        when(repository.findById(id)).thenReturn(Optional.of(coupon));

        service.delete(id);

        assertTrue(coupon.isDeleted());
        verify(repository, times(1)).save(coupon);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCoupon() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(id));
        assertEquals("Coupon not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingAlreadyDeletedCoupon() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.create(
                "ABC123",
                "Test",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );
        coupon.delete();

        when(repository.findById(id)).thenReturn(Optional.of(coupon));

        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(id));
        assertEquals("Coupon already deleted", exception.getMessage());
    }
}