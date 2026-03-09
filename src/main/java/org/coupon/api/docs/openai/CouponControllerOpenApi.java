package org.coupon.api.docs.openai;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.coupon.api.dto.CouponRequestDTO;
import org.coupon.api.dto.CouponResponseDTO;
import java.util.List;
import java.util.UUID;

@Tag(name = "Coupon")
public interface CouponControllerOpenApi {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized",content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden",content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content())
    })
    @Operation(summary = "Create coupon", description = "Creates a new discount coupon")
    CouponResponseDTO create(
            CouponRequestDTO request);


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupons retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden",content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content())
    })
    @Operation(summary = "List coupons", description = "Returns all available coupons")
    List<CouponResponseDTO> findAll();


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon found"),
            @ApiResponse(responseCode = "404", description = "Coupon not found",content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized",content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden",content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content())
    })
    @Operation(summary = "Find coupon by id", description = "Returns a coupon by its id")
    CouponResponseDTO findById(
            @Parameter(description = "Coupon ID", example = "1", required = true)
            UUID id);


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon updated successfully"),
            @ApiResponse(responseCode = "404", description = "Coupon not found",content = @Content()),
            @ApiResponse(responseCode = "400", description = "Invalid request",content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized",content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden",content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content())
    })
    @Operation(summary = "Update coupon", description = "Updates an existing coupon")
    CouponResponseDTO update(
            @Parameter(description = "Coupon ID", example = "1", required = true)
            UUID id,
            CouponRequestDTO request);


    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Coupon deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Coupon not found",content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized",content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden",content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content())
    })
    @Operation(summary = "Delete coupon", description = "Deletes a coupon by id")
    void delete(
            @Parameter(description = "Coupon ID", example = "1", required = true)
            UUID id);
}
