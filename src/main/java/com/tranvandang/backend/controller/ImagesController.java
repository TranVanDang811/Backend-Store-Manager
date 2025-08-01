package com.tranvandang.backend.controller;

import com.tranvandang.backend.dto.request.ApiResponse;
import com.tranvandang.backend.dto.response.ImagesResponse;
import com.tranvandang.backend.service.ImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImagesController {
    private final ImagesService imagesService;

    @PostMapping("/slider")
    public ApiResponse<ImagesResponse> uploadImage(
            @RequestParam String sliderId,
            @RequestParam("file") MultipartFile file
    ) {
        ImagesResponse response = imagesService.createImage(sliderId, file);
        return ApiResponse.<ImagesResponse>builder()
                .result(response)
                .message("Upload image successfully")
                .build();
    }

    @DeleteMapping("/slider/{imageId}")
    public ApiResponse<Void> deleteImage(@PathVariable String imageId) {
        imagesService.deleteImage(imageId);
        return ApiResponse.<Void>builder().message("Delete image successfully").build();
    }

    @PostMapping("/brand")
    public ApiResponse<ImagesResponse> uploadImageBrand(
            @RequestParam String brandId,
            @RequestParam("file") MultipartFile file
    ) {
        ImagesResponse response = imagesService.createImageBrand(brandId, file);
        return ApiResponse.<ImagesResponse>builder()
                .result(response)
                .message("Upload image successfully")
                .build();
    }

    @DeleteMapping("/brand/{imageId}")
    public ApiResponse<Void> deleteImageBrand(@PathVariable String imageId) {
        imagesService.deleteImageBrand(imageId);
        return ApiResponse.<Void>builder().message("Delete image successfully").build();
    }
}
