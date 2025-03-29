package com.tranvandang.backend.entity;

import com.tranvandang.backend.util.ShippingMethod;
import com.tranvandang.backend.util.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Shipping extends AbstractEntity{

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;

    String trackingNumber;

    @Enumerated(EnumType.STRING)
    ShippingMethod shippingMethod;

    @Enumerated(EnumType.STRING)
    ShippingStatus status; // Trạng thái vận chuyển
}
