package com.tranvandang.backend.dto.request;

import com.tranvandang.backend.util.ChangerStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.awt.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SliderRequest {
    ImagesRequest images;
    String title;
    String description;
    ChangerStatus status;
}
