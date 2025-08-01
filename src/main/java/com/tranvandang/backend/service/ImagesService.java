package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.response.ImagesResponse;
import com.tranvandang.backend.entity.Brand;
import com.tranvandang.backend.entity.CloudinaryUploadResult;
import com.tranvandang.backend.entity.Images;
import com.tranvandang.backend.entity.Slider;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.ImagesMapper;
import com.tranvandang.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImagesService {
    ImagesRepository imageRepository;
    SliderRepository sliderRepository;
    BrandRepository brandRepository;
    CloudinaryService cloudinaryService;
    ImagesMapper imageMapper;
    public ImagesResponse createImage(String sliderId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        Slider slider = sliderRepository.findById(sliderId)
                .orElseThrow(() -> new AppException(ErrorCode.SLIDER_NOT_FOUND));

        CloudinaryUploadResult result = cloudinaryService.uploadImage(file);
        if (result.getUrl() == null || result.getUrl().isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        Images image = Images.builder()
                .slider(slider)
                .imageUrl(result.getUrl())
                .publicId(result.getPublicId())
                .createdAt(new Date())
                .build();

        slider.setImages(image);
        slider.setImageUrl(result.getUrl());
        Images savedImage = imageRepository.save(image);

        return imageMapper.toResponse(savedImage);
    }

    @Transactional
    public void deleteImage(String imageId) {
        // Tìm ảnh
        Images image = imageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));

        // Lấy Slider liên kết
        Slider slider = image.getSlider();
        if (slider != null) {
            // Gỡ liên kết ảnh chính nếu URL trùng khớp
            if (image.getImageUrl().equals(slider.getImageUrl())) {
                slider.setImageUrl(null);
            }

            // Gỡ quan hệ @OneToOne giữa Slider và Image
            slider.setImages(null);

            // Lưu lại Slider để orphanRemoval = true hoạt động
            sliderRepository.save(slider);
        }

        // Xoá ảnh trên Cloudinary
        if (image.getPublicId() != null && !image.getPublicId().isBlank()) {
            cloudinaryService.deleteImage(image.getPublicId());
        }


    }

    public ImagesResponse createImageBrand(String brandId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        CloudinaryUploadResult result = cloudinaryService.uploadImage(file);
        if (result.getUrl() == null || result.getUrl().isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        Images image = Images.builder()
                .brand(brand)
                .imageUrl(result.getUrl())
                .publicId(result.getPublicId())
                .createdAt(new Date())
                .build();

        brand.setImages(image);
        brand.setImageUrl(result.getUrl());
        Images savedImage = imageRepository.save(image);

        return imageMapper.toResponse(savedImage);
    }

    @Transactional
    public void deleteImageBrand(String imageId) {
        // Tìm ảnh
        Images image = imageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));

        // Lấy Brand liên kết
        Brand brand = image.getBrand();
        if (brand != null) {
            // Gỡ liên kết ảnh chính nếu URL trùng khớp
            if (image.getImageUrl().equals(brand.getImageUrl())) {
                brand.setImageUrl(null);
            }

            // Gỡ quan hệ @OneToOne giữa Brand và Image
            brand.setImages(null);

            // Lưu lại Brand để orphanRemoval = true hoạt động
            brandRepository.save(brand);
        }

        // Xoá ảnh trên Cloudinary
        if (image.getPublicId() != null && !image.getPublicId().isBlank()) {
            cloudinaryService.deleteImage(image.getPublicId());
        }


    }


}
