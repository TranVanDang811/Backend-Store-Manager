package com.tranvandang.backend.dto.request;

import com.tranvandang.backend.util.ChangerStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SliderRequest {
    String imageUrl;
    String title;
    String description;
    ChangerStatus status;
}
