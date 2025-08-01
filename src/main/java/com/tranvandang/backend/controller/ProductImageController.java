package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.response.ProductImageResponse;
import com.tranvandang.backend.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("/multiple")
    public ApiResponse<List<ProductImageResponse>> uploadImages(
            @RequestParam("productId") String productId,
            @RequestParam("files") MultipartFile[] files) {
        return ApiResponse.<List<ProductImageResponse>>builder()
                .result(productImageService.createImages(productId, files))
                .build();
    }

    @GetMapping("/by-product/{productId}")
    public ApiResponse<List<ProductImageResponse>> getImagesByProductId(@PathVariable String productId) {
        return ApiResponse.<List<ProductImageResponse>>builder()
                .result(productImageService.getImagesByProductId(productId))
                .build();
    }

    @DeleteMapping("/{imageId}")
    public ApiResponse<Void> deleteImage(@PathVariable String imageId) {
        productImageService.deleteImageById(imageId);
        return ApiResponse.<Void>builder().message("Delete image successfully").build();
    }

    @DeleteMapping
    public ApiResponse<Void> deleteImages(@RequestBody List<String> imageIds) {
        productImageService.deleteImagesByIds(imageIds);
        return ApiResponse.<Void>builder().message("Delete all image successfully").build();
    }

    @PatchMapping("/{id}/set-main")
    public ApiResponse<ProductImageResponse> setMainImage(@PathVariable String id) {
        return ApiResponse.<ProductImageResponse>builder()
                .result(productImageService.setMainImage(id))
                .build();
    }



}
