package com.tranvandang.backend.dto.request;

import com.tranvandang.backend.util.BrandStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandRequest {
    String name;
    ImagesRequest images;
    BrandStatus status;
}
