package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.response.ProductImageResponse;
import com.tranvandang.backend.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("/multiple")
    public ResponseEntity<List<ProductImageResponse>> uploadImages(
            @RequestParam("productId") String productId,
            @RequestParam("files") MultipartFile[] files) {
        List<ProductImageResponse> responses = productImageService.createImages(productId, files);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<ProductImageResponse>> getImagesByProductId(@PathVariable String productId) {
        List<ProductImageResponse> responses = productImageService.getImagesByProductId(productId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{imageId}")
    public ApiResponse<Void> deleteImage(@PathVariable String imageId) {
        productImageService.deleteImageById(imageId);
        return ApiResponse.<Void>builder().message("Delete image successfully").build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteImages(@RequestBody List<String> imageIds) {
        productImageService.deleteImagesByIds(imageIds);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/set-main")
    public ResponseEntity<ProductImageResponse> setMainImage(@PathVariable String id) {
        ProductImageResponse response = productImageService.setMainImage(id);
        return ResponseEntity.ok(response);
    }



}
