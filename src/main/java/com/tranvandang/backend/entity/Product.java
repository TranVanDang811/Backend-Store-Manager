package com.tranvandang.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tranvandang.backend.util.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Product extends AbstractEntity {

    @Column(nullable = false, unique = true)
    String name;  // Tên điện thoại

    @Column(length = 2000)
    String description;  // Mô tả sản phẩm

    @Column(nullable = false)
    BigDecimal price;  // Giá bán

    @Column(nullable = false)
    Integer quantity;  // Số lượng tồn kho

    @Column(unique = true, nullable = false)
    String sku;  // Mã SKU duy nhất cho từng model

    String thumbnailUrl;  // Ảnh đại diện sản phẩm

    @Enumerated(EnumType.STRING)
    ProductStatus status;  // Trạng thái (CÒN HÀNG, HẾT HÀNG, NGỪNG KINH DOANH)

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Set<ProductImage> images = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    Brand brand; // Thương hiệu điện thoại

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;  // Danh mục sản phẩm

}
