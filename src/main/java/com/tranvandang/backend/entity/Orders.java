package com.tranvandang.backend.entity;

import com.tranvandang.backend.util.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Orders extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;  // Người đặt hàng

    @Column(nullable = false)
    BigDecimal totalPrice;  // Tổng tiền đơn hàng

    BigDecimal discountAmount;   // Số tiền giảm
    BigDecimal finalAmount;      // Tổng tiền thực trả

    @Column(name = "discount_code")
    String discountCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status;  // Trạng thái đơn hàng

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
    Set<OrderDetail> orderDetails;  // Danh sách sản phẩm trong đơn
}
