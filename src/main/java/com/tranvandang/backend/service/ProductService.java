package com.tranvandang.backend.service;

import com.tranvandang.backend.constant.PredefinedRole;
import com.tranvandang.backend.dto.request.ProductRequest;
import com.tranvandang.backend.dto.response.ProductResponse;
import com.tranvandang.backend.entity.*;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.ProductMapper;
import com.tranvandang.backend.repository.BrandRepository;
import com.tranvandang.backend.repository.CategoryRepository;
import com.tranvandang.backend.repository.ProductImageRepository;
import com.tranvandang.backend.repository.ProductRepository;
import com.tranvandang.backend.util.ProductStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    private final CategoryRepository categoryRepository;
    ProductRepository productRepository;
    ProductImageRepository imageRepository;
    CloudinaryService cloudinaryService;
    ProductMapper productMapper;
    BrandRepository brandRepository;

    public ProductResponse create(ProductRequest request, MultipartFile[] images) {


        Product product = productMapper.toProduct(request);

        Brand brand = brandRepository.findByName(request.getBrandName())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        product.setBrand(brand);


        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        List<ProductImage> productImages = new ArrayList<>();
        String thumbnailUrl = null;

        if (images != null && images.length > 0) {
            for (int i = 0; i < images.length; i++) {
                String imageUrl = cloudinaryService.uploadImage(images[i]);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    productImages.add(new ProductImage(null, product, imageUrl, new Date()));
                    if (i == 0) thumbnailUrl = imageUrl; // Gán thumbnail từ ảnh đầu tiên
                }
            }
        }
        product.setThumbnailUrl(thumbnailUrl);
        log.info("Thumbnail URL before saving: {}", product.getThumbnailUrl());

        product.setImages(new HashSet<>(productImages));
        product = productRepository.save(product);
        imageRepository.saveAll(productImages);

        log.info("Saved product: {}", product);

        ProductResponse response = productMapper.toProductResponse(product);
        response.setImages(productMapper.toProductImageResponses(product.getImages()));

        return response;
    }

    public List<ProductResponse> getProductsByBrand(String brandName) {
        Brand brand = brandRepository.findByName(brandName)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        List<Product> products = productRepository.findByBrand_Name(brandName);

        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<Product> products = productRepository.findByCategory_Name(categoryName);

        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public Page<ProductResponse> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable)
                .map(productMapper::toProductResponse);
    }

    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    @PreAuthorize("hasRole('USER')")
    public ProductResponse getProduct(String productId) {
        return productMapper.toProductResponse(
                productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(String productId) {
        productRepository.deleteById(productId);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse changerStatus(String productId, ProductStatus status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        product.setStatus(status);

        return productMapper.toProductResponse(productRepository.save(product));
    }
}
