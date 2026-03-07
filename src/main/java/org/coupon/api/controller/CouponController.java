package org.coupon.api.controller;

import org.coupon.api.dto.CouponRequestDTO;
import org.coupon.api.dto.CouponResponseDTO;
import org.coupon.api.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService service;

    public CouponController(CouponService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CouponResponseDTO> create(
            @RequestBody CouponRequestDTO request
    ){

        CouponResponseDTO response = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}