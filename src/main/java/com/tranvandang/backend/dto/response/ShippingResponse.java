package com.tranvandang.backend.dto.response;

import com.tranvandang.backend.util.ShippingMethod;
import com.tranvandang.backend.util.ShippingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingResponse {
    String id;
    String orderId;
    String trackingNumber;
    ShippingMethod shippingMethod;
    ShippingStatus status;
    Date createdAt;
    Date updateAt;
}
