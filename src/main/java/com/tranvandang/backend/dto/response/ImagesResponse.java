
package com.tranvandang.backend.dto.response;

import lombok.*;
        import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImagesResponse {
    String id;
    String imageUrl;
    String sliderId;
    String brandId;
}
