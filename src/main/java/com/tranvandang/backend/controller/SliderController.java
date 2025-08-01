package com.tranvandang.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.request.ProductUpdateRequest;
import com.tranvandang.backend.dto.request.SliderRequest;
import com.tranvandang.backend.dto.request.UpdateSliderRequest;
import com.tranvandang.backend.dto.response.ProductResponse;
import com.tranvandang.backend.dto.response.SliderResponse;
import com.tranvandang.backend.dto.response.SupplierResponse;
import com.tranvandang.backend.dto.response.UserResponse;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.service.SliderService;
import com.tranvandang.backend.util.ChangerStatus;
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
@RequestMapping("/sliders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SliderController {
    SliderService sliderService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<SliderResponse> create(
            @RequestPart("slider") String sliderJson,
            @RequestPart(value = "images", required = false) MultipartFile images) {
        ObjectMapper objectMapper = new ObjectMapper();
        SliderRequest sliderRequest;
        try {
            sliderRequest = objectMapper.readValue(sliderJson, SliderRequest.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_JSON);
        }
        return ApiResponse.<SliderResponse>builder()
                .result(sliderService.create(sliderRequest, images))
                .build();
    }


    @PutMapping("/{sliderId}")
    ApiResponse<SliderResponse> updateSlider(@PathVariable String sliderId, @RequestBody UpdateSliderRequest request) {
        return ApiResponse.<SliderResponse>builder()
                .result(sliderService.updateSlider(sliderId, request))
                .build();
    }



    @GetMapping
    ApiResponse<List<SliderResponse>> getAll() {
        return ApiResponse.<List<SliderResponse>>builder()
                .result(sliderService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<SliderResponse> getById(@PathVariable String id) {
        return ApiResponse.<SliderResponse>builder().result(sliderService.getById(id)).build();
    }


    @PatchMapping("/{sliderId}")
    public ApiResponse<SliderResponse> changerStatus(
            @PathVariable String sliderId,
            @RequestParam ChangerStatus status) {

        log.info("Request changer slider status, sliderId = {}", sliderId);

        SliderResponse response = sliderService.changerStatus(sliderId, status);

        return ApiResponse.<SliderResponse>builder()
                .result(response)
                .build();
    }

    @DeleteMapping("/{slider}")
    ApiResponse<Void> delete(@PathVariable String slider) {
        sliderService.delete(slider);
        return ApiResponse.<Void>builder().message("Delete successfully").build();

    }
}
