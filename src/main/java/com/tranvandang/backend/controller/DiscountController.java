package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.DiscountRequest;
import com.tranvandang.backend.dto.response.DiscountResponse;
import com.tranvandang.backend.service.DiscountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountController {
    final DiscountService discountService;

    //Created
    @PostMapping
    public ApiResponse<DiscountResponse> createDiscount(@RequestBody DiscountRequest request) {
        return ApiResponse.<DiscountResponse>builder()
                .result(discountService.createDiscount(request))
                .build();
    }

    //Update discount
    @PutMapping("/{id}")
    public ApiResponse<DiscountResponse> updateDiscount(@PathVariable String id,
                                                           @RequestBody DiscountRequest request) {
        return ApiResponse.<DiscountResponse>builder()
                .result(discountService.updateDiscount(id, request))
                .build();
    }

    //Delete discount
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDiscount(@PathVariable String id) {
        discountService.deleteDiscount(id);
        return ApiResponse.<Void>builder().message("Delete successfully").build();
    }

    //Get discount by id
    @GetMapping("/{id}")
    public ApiResponse<DiscountResponse> getDiscountById(@PathVariable String id) {
        return ApiResponse.<DiscountResponse>builder()
                .result(discountService.getDiscountById(id))
                .build();
    }

    //Get all discounts
    @GetMapping
    public ApiResponse<List<DiscountResponse>> getAllDiscounts() {
        return ApiResponse.<List<DiscountResponse>>builder()
                .result(discountService.getAllDiscounts())
                .build();
    }

    // Get a list of active coupon codes
    @GetMapping("/active")
    public ApiResponse<List<DiscountResponse>> getActiveDiscounts() {
        return ApiResponse.<List<DiscountResponse>>builder()
                .result(discountService.getActiveDiscounts())
                .build();
    }

    // Find discount code by code
    @GetMapping("/code/{code}")
    public ApiResponse<DiscountResponse> getByCode(@PathVariable String code) {
        return ApiResponse.<DiscountResponse>builder()
                .result(discountService.getByCode(code))
                .build();
    }
}
