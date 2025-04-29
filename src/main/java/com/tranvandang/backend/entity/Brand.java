package com.tranvandang.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Brand extends AbstractEntity{

    String name;  // Tên thương hiệu

    String logoUrl;  // Logo thương hiệu
    String publicId;
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Product> products = new HashSet<>();

}
