package com.tranvandang.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;  // Đơn hàng chứa sản phẩm này

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;  // ID của sản phẩm

    @Column(nullable = false)
    String productName;  // Tên sản phẩm tại thời điểm đặt hàng

    @Column(nullable = false)
    BigDecimal productPrice;  // Giá sản phẩm tại thời điểm đặt hàng

    @Column(nullable = false)
    Integer quantity;  // Số lượng sản phẩm mua

    @Column(nullable = false)
    BigDecimal totalPrice;  // Tổng tiền cho sản phẩm (productPrice * quantity)
}
