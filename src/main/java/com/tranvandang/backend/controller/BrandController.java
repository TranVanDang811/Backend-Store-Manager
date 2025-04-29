package com.tranvandang.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.BrandRequest;
import com.tranvandang.backend.dto.response.BrandResponse;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.service.BrandService;
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

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    // API nhận request POST với dữ liệu dạng multipart/form-data
    public ApiResponse<BrandResponse> createBrand(
            @RequestPart("brand") String brandJson,  // Nhận JSON dưới dạng String từ request
            @RequestPart(value = "images", required = false) MultipartFile images) {  // Nhận danh sách ảnh (nếu có)
        ObjectMapper objectMapper = new ObjectMapper();  // Khởi tạo ObjectMapper để chuyển đổi JSON thành object Java
        BrandRequest brandRequest;
        try {
            brandRequest = objectMapper.readValue(brandJson, BrandRequest.class);  // Chuyển JSON thành object BrandRequest
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_JSON);  // Ném lỗi khi parse JSON thất bại
        }
        return ApiResponse.<BrandResponse>builder()  // Trả về API response dạng generic
                .result(brandService.create(brandRequest, images))  // Gọi service để tạo và nhận kết quả
                .build();
    }

    @GetMapping
    ApiResponse<List<BrandResponse>> getAll() {
        return ApiResponse.<List<BrandResponse>>builder()
                .result(brandService.getAll())
                .build();
    }

    @DeleteMapping("/{brand}")
    ApiResponse<Void> delete(@PathVariable String brand) {
        brandService.delete(brand);
        return ApiResponse.<Void>builder().build();
    }
}
