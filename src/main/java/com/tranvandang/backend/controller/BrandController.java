package com.tranvandang.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranvandang.backend.dto.request.*;
import com.tranvandang.backend.dto.response.BrandResponse;
import com.tranvandang.backend.dto.response.SliderResponse;
import com.tranvandang.backend.dto.response.UserResponse;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.service.BrandService;
import com.tranvandang.backend.util.BrandStatus;
import com.tranvandang.backend.util.UserStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandController {
    BrandService brandService;

    //Created brand
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<BrandResponse> createBrand(
            @RequestPart("brand") String brandJson,
            @RequestPart(value = "images", required = false) MultipartFile images) {
        ObjectMapper objectMapper = new ObjectMapper();
        BrandRequest brandRequest;
        try {
            brandRequest = objectMapper.readValue(brandJson, BrandRequest.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_JSON);
        }
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.create(brandRequest, images))
                .build();
    }

    //Update brand
    @PutMapping("/{brandId}")
    ApiResponse<BrandResponse> updateBrand(@PathVariable String brandId, @RequestBody BrandUpdateRequest request) {
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.updateBrand(brandId, request))
                .build();
    }

    //Change status
    @PatchMapping("/{brandId}")
    public ApiResponse<BrandResponse> changerStatus(
            @PathVariable String brandId,
            @RequestParam BrandStatus status) {

        log.info("Request changer user status, userId = {}", brandId);

        BrandResponse response = brandService.changerStatus(brandId, status);

        return ApiResponse.<BrandResponse>builder()
                .result(response)
                .build();
    }

    //Get brand by id
    @GetMapping("/{id}")
    ApiResponse<BrandResponse> getById(@PathVariable String id) {
        return ApiResponse.<BrandResponse>builder().result(brandService.getById(id)).build();
    }

    //Get all brand
    @GetMapping
    ApiResponse<List<BrandResponse>> getAll() {
        return ApiResponse.<List<BrandResponse>>builder()
                .result(brandService.getAll())
                .build();
    }

    //Delete brand
    @DeleteMapping("/{brand}")
    ApiResponse<Void> delete(@PathVariable String brand) {
        brandService.delete(brand);
        return ApiResponse.<Void>builder().message("Delete successfully").build();
    }
}
