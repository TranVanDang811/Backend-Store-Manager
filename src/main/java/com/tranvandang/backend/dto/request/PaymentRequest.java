package com.tranvandang.backend.dto.request;

import com.tranvandang.backend.util.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    String orderId;
    BigDecimal amount;
    PaymentMethod paymentMethod;
    String cardToken; // Chỉ dùng cho CREDIT_CARD
}
