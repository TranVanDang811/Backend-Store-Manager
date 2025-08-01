package com.tranvandang.backend.dto.response;

import com.tranvandang.backend.util.BrandStatus;
import com.tranvandang.backend.util.ChangerStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandResponse {
    String name;
    String imageUrl;
    BrandStatus status;
}
