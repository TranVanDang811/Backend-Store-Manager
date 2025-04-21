package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.response.ProductImageResponse;
import com.tranvandang.backend.entity.CloudinaryUploadResult;
import com.tranvandang.backend.entity.Product;
import com.tranvandang.backend.entity.ProductImage;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.ProductImageMapper;
import com.tranvandang.backend.repository.ProductImageRepository;
import com.tranvandang.backend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImageService {

    ProductImageRepository productImageRepository;
    ProductRepository productRepository;
    CloudinaryService cloudinaryService;
    ProductImageMapper productImageMapper;

    public List<ProductImageResponse> createImages(String productId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        List<ProductImage> imageEntities = new ArrayList<>();

        for (MultipartFile file : files) {
            CloudinaryUploadResult result = cloudinaryService.uploadImage(file);
            if (result.getUrl() != null && !result.getUrl().isEmpty()) {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(result.getUrl())
                        .publicId(result.getPublicId())
                        .createdAt(new Date())
                        .build();
                imageEntities.add(image);
            }
        }

        if (imageEntities.isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        // Lưu tất cả ảnh vào DB
        List<ProductImage> savedImages = productImageRepository.saveAll(imageEntities);

        // Trả về danh sách response
        return savedImages.stream()
                .map(productImageMapper::toResponse)
                .toList();
    }

    public List<ProductImageResponse> getImagesByProductId(String productId) {
        // Kiểm tra sản phẩm tồn tại
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Lấy danh sách ảnh từ repository
        List<ProductImage> images = productImageRepository.findByProductId(productId);

        // Chuyển sang response DTO
        return images.stream()
                .map(productImageMapper::toResponse)
                .toList();
    }

    public void deleteImageById(String imageId) {
        // Tìm ảnh theo ID
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND)); // Xử lý lỗi nếu ảnh không tồn tại

        // Kiểm tra nếu ảnh là ảnh đại diện (thumbnail) của sản phẩm
        Product product = image.getProduct();
        if (product.getThumbnailUrl() != null && product.getThumbnailUrl().equals(image.getImageUrl())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_THUMBNAIL);
        }

        // Xóa ảnh trên Cloudinary nếu có publicId
        if (image.getPublicId() != null && !image.getPublicId().isBlank()) {
            cloudinaryService.deleteImage(image.getPublicId());
        }

        // Xóa ảnh trong DB
        productImageRepository.delete(image);
    }


    public void deleteImagesByIds(List<String> imageIds) {
        List<ProductImage> images = productImageRepository.findAllById(imageIds);

        if (images.isEmpty()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND); // hoặc tự define lỗi riêng
        }

        for (ProductImage image : images) {
            if (image.getPublicId() != null && !image.getPublicId().isBlank()) {
                cloudinaryService.deleteImage(image.getPublicId());
            }
        }

        productImageRepository.deleteAll(images);
    }

    @Transactional
    public ProductImageResponse setMainImage(String imageId) {
        // Tìm ảnh theo ID
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Lấy product tương ứng và cập nhật thumbnail
        Product product = image.getProduct();
        product.setThumbnailUrl(image.getImageUrl());

        productRepository.save(product);

        // Trả response cho client
        return productImageMapper.toResponse(image);
    }


}
