package com.tranvandang.backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRequest {
    String name;
    double discountRate;
    LocalDateTime startDate;
    LocalDateTime endDate;
    boolean active;
    String code;
}
