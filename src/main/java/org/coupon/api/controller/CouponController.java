package org.coupon.api.controller;

import jakarta.validation.Valid;
import org.coupon.api.docs.openai.CouponControllerOpenApi;
import org.coupon.api.dto.CouponRequestDTO;
import org.coupon.api.dto.CouponResponseDTO;
import org.coupon.api.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coupons")
public class CouponController implements CouponControllerOpenApi {

    private final CouponService service;

    public CouponController(CouponService service) {
        this.service = service;
    }
    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CouponResponseDTO create(
            @Valid @RequestBody CouponRequestDTO request
    ){
        return service.create(request);
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CouponResponseDTO> findAll(){
        return service.findAll();
    }

    @Override
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CouponResponseDTO findById(@PathVariable UUID id){
        return service.findById(id);
    }

    @Override
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CouponResponseDTO update(@PathVariable UUID id, @RequestBody CouponRequestDTO request){
        return service.update(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){
        service.delete(id);
    }
}