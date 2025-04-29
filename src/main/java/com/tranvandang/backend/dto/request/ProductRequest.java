package com.tranvandang.backend.dto.request;

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
public class ProductRequest {
    String name;
    String description;
    BigDecimal price;
    Integer quantity;
    String sku;
    ProductStatus status;
    String brandName;
    String categoryName;
    Set<ProductImageRequest> images;
}
