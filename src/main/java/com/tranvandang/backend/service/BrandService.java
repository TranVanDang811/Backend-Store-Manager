package com.tranvandang.backend.service;



import com.tranvandang.backend.dto.request.BrandRequest;
import com.tranvandang.backend.dto.response.BrandResponse;
import com.tranvandang.backend.entity.Brand;
import com.tranvandang.backend.entity.CloudinaryUploadResult;
import com.tranvandang.backend.mapper.BrandMapper;
import com.tranvandang.backend.repository.BrandRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandService {
    BrandRepository brandRepository;
    BrandMapper brandMapper;
    CloudinaryService cloudinaryService;
    public BrandResponse create(BrandRequest request, MultipartFile image) {
        Brand brand = brandMapper.toBrand(request);

        if (image != null && !image.isEmpty()) {
            CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(image);
            if (uploadResult != null && uploadResult.getUrl() != null) {
                brand.setLogoUrl(uploadResult.getUrl());
                brand.setPublicId(uploadResult.getPublicId());
            }
        }

        brand = brandRepository.save(brand);
        log.info("Saved brand: {}", brand);

        return brandMapper.toBrandResponse(brand);
    }

    public List<BrandResponse> getAll() {
        var brands = brandRepository.findAll();
        return brands.stream().map(brandMapper::toBrandResponse).toList();
    }



    public void delete(String brand) {
        brandRepository.deleteById(brand);
    }
}
