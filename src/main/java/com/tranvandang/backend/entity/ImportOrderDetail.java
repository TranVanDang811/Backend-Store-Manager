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
public class ImportOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    int quantity;

    BigDecimal importPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_order_id")
    ImportOrder importOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;
}
