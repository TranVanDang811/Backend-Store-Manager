package com.tranvandang.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    String userId;
    String userName;
    BigDecimal totalPrice;
    BigDecimal discountAmount;
    BigDecimal finalAmount;
    String status;
    Set<OrderDetailResponse> orderDetails;
}
