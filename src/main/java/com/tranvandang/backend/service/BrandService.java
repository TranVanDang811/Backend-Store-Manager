package com.tranvandang.backend.service;



import com.tranvandang.backend.dto.request.BrandRequest;
import com.tranvandang.backend.dto.request.BrandUpdateRequest;
import com.tranvandang.backend.dto.request.SliderRequest;
import com.tranvandang.backend.dto.request.UpdateSliderRequest;
import com.tranvandang.backend.dto.response.BrandResponse;
import com.tranvandang.backend.dto.response.SliderResponse;
import com.tranvandang.backend.dto.response.UserResponse;
import com.tranvandang.backend.entity.*;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.BrandMapper;
import com.tranvandang.backend.repository.BrandRepository;
import com.tranvandang.backend.util.BrandStatus;
import com.tranvandang.backend.util.UserStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.Date;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandService {
    BrandRepository brandRepository;
    BrandMapper brandMapper;
    CloudinaryService cloudinaryService;


    @PreAuthorize("hasRole('ADMIN')")
    public BrandResponse create(BrandRequest request, MultipartFile file) {
        Brand brand = brandMapper.toBrand(request);

        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

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

        // Gán ảnh vào brand
        brand.setImages(image);
        brand.setImageUrl(result.getUrl());
        // Lưu Slider và cascade lưu luôn ảnh
        brand = brandRepository.save(brand);

        log.info("Saved brand: {}", brand);

        return brandMapper.toBrandResponse(brand);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BrandResponse updateBrand(String brandId, BrandUpdateRequest request) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        // MapStruct tự động cập nhật các trường từ request
        brandMapper.updateBrandFromRequest(request, brand);

        // Lưu lại vào DB
        Brand updateBrand = brandRepository.save(brand);

        return brandMapper.toBrandResponse(updateBrand);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BrandResponse getById(String id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        return brandMapper.toBrandResponse(brand);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<BrandResponse> getPaged(String keyword, Pageable pageable) {
        Page<Brand> page = brandRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return page.map(brandMapper::toBrandResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BrandResponse changerStatus(String brandId, BrandStatus status) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        brand.setStatus(status);

        return brandMapper.toBrandResponse(brandRepository.save(brand));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BrandResponse> getAll() {
        var brands = brandRepository.findAll();
        return brands.stream().map(brandMapper::toBrandResponse).toList();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String brand) {
        brandRepository.deleteById(brand);
    }
}
