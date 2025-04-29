package com.tranvandang.backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilterRequest {
    String categoryName;
    String brandName;
    String sortByPrice;
    String sortByName;
    String sortByCreatedAt;
    String status;
    int page;
    int size;
}
