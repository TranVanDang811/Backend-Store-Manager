package com.tranvandang.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportOrderStatisticResponse {
    long totalOrders;
    long totalProducts;
    BigDecimal totalAmount;

}
