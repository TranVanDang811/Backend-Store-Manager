package com.tranvandang.backend.mapper;

import com.tranvandang.backend.dto.request.ProductImageRequest;
import com.tranvandang.backend.dto.response.ProductImageResponse;
import com.tranvandang.backend.entity.ProductImage;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mapping(target = "productId", source = "product.id")
    ProductImageResponse toResponse(ProductImage productImage);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "product", ignore = true) // xử lý thủ công trong service
    ProductImage toEntity(ProductImageRequest request);
}
