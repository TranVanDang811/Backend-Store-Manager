package com.tranvandang.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.BrandRequest;
import com.tranvandang.backend.dto.request.SliderRequest;
import com.tranvandang.backend.dto.response.BrandResponse;
import com.tranvandang.backend.dto.response.SliderResponse;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.service.BrandService;
import com.tranvandang.backend.service.SliderService;
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
@RequestMapping("/sliders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SliderController {
    SliderService sliderService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    // API nhận request POST với dữ liệu dạng multipart/form-data
    public ApiResponse<SliderResponse> createBrand(
            @RequestPart("slider") String sliderJson,  // Nhận JSON dưới dạng String từ request
            @RequestPart(value = "images", required = false) MultipartFile images) {  // Nhận danh sách ảnh (nếu có)
        ObjectMapper objectMapper = new ObjectMapper();  // Khởi tạo ObjectMapper để chuyển đổi JSON thành object Java
        SliderRequest sliderRequest;
        try {
            sliderRequest = objectMapper.readValue(sliderJson, SliderRequest.class);  // Chuyển JSON thành object BrandRequest
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_JSON);  // Ném lỗi khi parse JSON thất bại
        }
        return ApiResponse.<SliderResponse>builder()  // Trả về API response dạng generic
                .result(sliderService.create(sliderRequest, images))  // Gọi service để tạo và nhận kết quả
                .build();
    }

    @GetMapping
    ApiResponse<List<SliderResponse>> getAll() {
        return ApiResponse.<List<SliderResponse>>builder()
                .result(sliderService.getAll())
                .build();
    }

    @DeleteMapping("/{slider}")
    ApiResponse<Void> delete(@PathVariable String slider) {
        sliderService.delete(slider);
        return ApiResponse.<Void>builder().build();
    }
}
