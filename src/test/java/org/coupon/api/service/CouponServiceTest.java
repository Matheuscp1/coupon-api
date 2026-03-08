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
import java.util.List;
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

    @Test
    void shouldReturnAllCoupons() {
        Coupon coupon1 = Coupon.create("ABC123", "Desc1", BigDecimal.TEN, LocalDate.now().plusDays(1), true);
        Coupon coupon2 = Coupon.create("ABC124", "Desc2", BigDecimal.valueOf(20), LocalDate.now().plusDays(2), true);

        when(repository.findAll()).thenReturn(List.of(coupon1, coupon2));

        List<CouponResponseDTO> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("ABC123", result.get(0).code());
        assertEquals("ABC124", result.get(1).code());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldReturnCouponById() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.create("ABC123", "Desc3", BigDecimal.valueOf(30), LocalDate.now().plusDays(3), true);

        when(repository.findById(id)).thenReturn(Optional.of(coupon));

        CouponResponseDTO result = service.findById(id);

        assertEquals("ABC123", result.code());
        assertEquals("Desc3", result.description());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenCouponNotFoundById() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.findById(id));
        assertEquals("Coupon not found", exception.getMessage());
    }

    @Test
    void shouldUpdateCouponSuccessfully() {
        UUID id = UUID.randomUUID();
        Coupon existingCoupon = Coupon.create("ABC123", "Old Desc", BigDecimal.valueOf(15), LocalDate.now().plusDays(1), true);

        CouponRequestDTO updateRequest = new CouponRequestDTO("ABC124", "New Desc", BigDecimal.valueOf(25), LocalDate.now().plusDays(5), true);

        when(repository.findById(id)).thenReturn(Optional.of(existingCoupon));

        CouponResponseDTO result = service.update(id, updateRequest);

        assertEquals("ABC124", result.code());
        assertEquals("New Desc", result.description());
        assertEquals(BigDecimal.valueOf(25), result.discountValue());
        verify(repository, times(1)).save(any(Coupon.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCoupon() {
        UUID id = UUID.randomUUID();
        CouponRequestDTO updateRequest = new CouponRequestDTO("ABC123", "New Desc", BigDecimal.valueOf(25), LocalDate.now().plusDays(5), true);

        when(repository.findById(id)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.update(id, updateRequest));
        assertEquals("Coupon not found", exception.getMessage());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRepositoryFailsOnCreate() {
        CouponRequestDTO request = new CouponRequestDTO(
                "ABC123",
                "Test coupon",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );

        doThrow(new RuntimeException("DB error")).when(repository).save(any(Coupon.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.create(request));
        assertEquals("Internal error during coupon creation", exception.getMessage());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRepositoryFailsOnFindAll() {
        when(repository.findAll()).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.findAll());
        assertEquals("Internal error during coupons fetch", exception.getMessage());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRepositoryFailsOnFindById() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.findById(id));
        assertEquals("Internal error during coupon fetch", exception.getMessage());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRepositoryFailsOnUpdate() {
        UUID id = UUID.randomUUID();
        CouponRequestDTO updateRequest = new CouponRequestDTO("ABC123", "Desc", BigDecimal.valueOf(10), LocalDate.now().plusDays(1), true);

        when(repository.findById(id)).thenReturn(Optional.of(Coupon.create("ABC123", "Old", BigDecimal.valueOf(5), LocalDate.now().plusDays(1), true)));
        doThrow(new RuntimeException("DB error")).when(repository).save(any(Coupon.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.update(id, updateRequest));
        assertEquals("Internal error during coupon update", exception.getMessage());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRepositoryFailsOnDelete() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.create("ABC123", "Test", BigDecimal.valueOf(10), LocalDate.now().plusDays(1), true);

        when(repository.findById(id)).thenReturn(Optional.of(coupon));
        doThrow(new RuntimeException("DB error")).when(repository).save(coupon);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.delete(id));
        assertEquals("Internal error during coupon deletion", exception.getMessage());
    }

    @Test
    void shouldThrowBusinessExceptionWhenCouponInvalid() {
        CouponRequestDTO request = new CouponRequestDTO(
                "A@B1",
                "Test coupon",
                BigDecimal.valueOf(10),
                LocalDate.now().plusDays(1),
                true
        );

        BusinessException exception = assertThrows(BusinessException.class, () -> service.create(request));
        assertEquals("Coupon code must have 6 characters after sanitization", exception.getMessage());
    }
}