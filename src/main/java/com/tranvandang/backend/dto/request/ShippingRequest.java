package com.tranvandang.backend.dto.request;

import com.tranvandang.backend.util.ShippingMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingRequest {
    String orderId;
    String trackingNumber;
    ShippingMethod shippingMethod;
}
