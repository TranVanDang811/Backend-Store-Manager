package com.tranvandang.backend.dto.response;

import com.tranvandang.backend.util.PaymentMethod;
import com.tranvandang.backend.util.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String id;
    String transactionId;
    BigDecimal amount;
    PaymentMethod paymentMethod;
    PaymentStatus status;
    String processedBy;
    LocalDateTime processedAt;
    String orderId;
    LocalDateTime createdAt;
    LocalDateTime updateAt;
}
