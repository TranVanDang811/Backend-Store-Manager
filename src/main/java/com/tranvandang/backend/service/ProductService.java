package com.tranvandang.backend.service;

import com.tranvandang.backend.constant.PredefinedRole;
import com.tranvandang.backend.dto.request.ProductRequest;
import com.tranvandang.backend.dto.request.ProductUpdateRequest;
import com.tranvandang.backend.dto.response.ProductResponse;
import com.tranvandang.backend.entity.*;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.ProductMapper;
import com.tranvandang.backend.repository.*;
import com.tranvandang.backend.util.ProductStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    CartItemRepository cartItemRepository;
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
                CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(images[i]);

                if (uploadResult != null && uploadResult.getUrl() != null) {
                    ProductImage image = ProductImage.builder()
                            .imageUrl(uploadResult.getUrl())
                            .publicId(uploadResult.getPublicId())
                            .product(product) // set lại quan hệ
                            .build();
                    productImages.add(image);

                    if (i == 0) thumbnailUrl = uploadResult.getUrl(); // ảnh đầu tiên làm thumbnail
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

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // MapStruct tự động cập nhật các trường từ request -> product
        productMapper.updateProductFromRequest(request, product);

        // Lưu lại vào DB
        Product updatedProduct = productRepository.save(product);

        return productMapper.toProductResponse(updatedProduct);
    }


    public Page<ProductResponse> getProducts(
            String categoryName,
            String brandName,
            String sortByPrice,
            String sortByName,
            String sortByCreatedAt,
            String status,
            int page,
            int size) {

        List<Sort.Order> orders = new ArrayList<>();

        if ("ASC".equalsIgnoreCase(sortByPrice)) orders.add(Sort.Order.asc("price"));
        if ("DESC".equalsIgnoreCase(sortByPrice)) orders.add(Sort.Order.desc("price"));

        if ("ASC".equalsIgnoreCase(sortByName)) orders.add(Sort.Order.asc("name"));
        if ("DESC".equalsIgnoreCase(sortByName)) orders.add(Sort.Order.desc("name"));

        if ("ASC".equalsIgnoreCase(sortByCreatedAt)) orders.add(Sort.Order.asc("createdAt"));
        if ("DESC".equalsIgnoreCase(sortByCreatedAt)) orders.add(Sort.Order.desc("createdAt"));

        Pageable pageable = orders.isEmpty()
                ? PageRequest.of(page, size)
                : PageRequest.of(page, size, Sort.by(orders));

        Page<Product> productPage;

        ProductStatus productStatus = null;
        if (status != null) {
            try {
                productStatus = ProductStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_STATUS); // hoặc return rỗng tuỳ anh
            }
        }

        if (categoryName != null && brandName != null && productStatus != null) {
            productPage = productRepository.findByCategory_NameAndBrand_NameAndStatus(categoryName, brandName, productStatus, pageable);
        } else if (categoryName != null && productStatus != null) {
            productPage = productRepository.findByCategory_NameAndStatus(categoryName, productStatus, pageable);
        } else if (brandName != null && productStatus != null) {
            productPage = productRepository.findByBrand_NameAndStatus(brandName, productStatus, pageable);
        } else if (productStatus != null) {
            productPage = productRepository.findByStatus(productStatus, pageable);
        } else if (categoryName != null && brandName != null) {
            productPage = productRepository.findByCategory_NameAndBrand_Name(categoryName, brandName, pageable);
        } else if (categoryName != null) {
            productPage = productRepository.findByCategory_Name(categoryName, pageable);
        } else if (brandName != null) {
            productPage = productRepository.findByBrand_Name(brandName, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.map(productMapper::toProductResponse);
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

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Xoá cart items trước để tránh lỗi FK
        cartItemRepository.deleteAllByProductId(productId);

        // Xoá ảnh trên Cloudinary
        for (ProductImage image : product.getImages()) {
            try {
                if (image.getPublicId() != null) {
                    cloudinaryService.deleteImage(image.getPublicId());
                }
            } catch (Exception e) {
                log.warn("Error deleting Cloudinary image: {}", image.getPublicId(), e);
            }
        }

        // Nếu đã cascade thì có thể bỏ dòng này
        imageRepository.deleteAll(product.getImages());

        // Xoá sản phẩm
        productRepository.delete(product);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProducts(List<String> productIds) {
        for (String productId : productIds) {
            deleteProduct(productId); // tái sử dụng hàm đã có
        }
    }


    public List<ProductResponse> getRelatedProducts(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        List<Product> relatedProducts = productRepository
                .findTop5ByCategoryAndIdNot(product.getCategory(), productId);

        return relatedProducts.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getProductStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalProducts", productRepository.count());
        stats.put("activeProducts", productRepository.countByStatus(ProductStatus.ACTIVE));
        stats.put("inactiveProducts", productRepository.countByStatus(ProductStatus.OUT_OF_STOCK));
        stats.put("pendingProducts", productRepository.countByStatus(ProductStatus.DISCONTINUED));

        return stats;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse changerStatus(String productId, ProductStatus status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        product.setStatus(status);

        return productMapper.toProductResponse(productRepository.save(product));
    }
}
