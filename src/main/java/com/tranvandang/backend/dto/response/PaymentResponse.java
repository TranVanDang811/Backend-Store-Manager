package com.tranvandang.backend.dto.response;

import com.tranvandang.backend.util.PaymentMethod;
import com.tranvandang.backend.util.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String id;
    String orderId;
    Double amount;
    PaymentMethod paymentMethod;
    PaymentStatus status;
    String transactionId;
}
