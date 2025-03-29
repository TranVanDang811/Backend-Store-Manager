package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.ProductImageRequest;
import com.tranvandang.backend.dto.request.ProductRequest;
import com.tranvandang.backend.dto.response.BrandResponse;
import com.tranvandang.backend.dto.response.CategoryResponse;
import com.tranvandang.backend.dto.response.ProductImageResponse;
import com.tranvandang.backend.dto.response.ProductResponse;
import com.tranvandang.backend.entity.Brand;
import com.tranvandang.backend.entity.Category;
import com.tranvandang.backend.entity.Product;
import com.tranvandang.backend.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "images", source = "images")
//    @Mapping(target = "brand", source = "brand")
    Product toProduct(ProductRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "thumbnailUrl", source = "thumbnailUrl")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "category", source = "category")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "imageUrl", source = "imageUrl") // Dùng đúng tên thuộc tính
    ProductImageResponse toProductImageResponse(ProductImage image);

    Set<ProductImageResponse> toProductImageResponses(Set<ProductImage> images);

    @Mapping(target = "name", source = "name")
    BrandResponse toBrandResponse(Brand brand);

    @Mapping(target = "name", source = "name")
    CategoryResponse toCategoryResponse(Category category);
}
