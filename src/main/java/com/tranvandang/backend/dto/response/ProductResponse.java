package com.tranvandang.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tranvandang.backend.entity.Brand;
import com.tranvandang.backend.entity.ProductImage;
import com.tranvandang.backend.util.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String name;
    String description;
    BigDecimal price;
    Integer quantity;
    String sku;
    ProductStatus status;
    String thumbnailUrl;

    CategoryResponse category;
    BrandResponse brand;
    Set<ProductImageResponse> images;
}
