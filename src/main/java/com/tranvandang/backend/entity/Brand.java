package com.tranvandang.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tranvandang.backend.util.BrandStatus;
import com.tranvandang.backend.util.ChangerStatus;
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

    String imageUrl;

    @OneToOne(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Images images;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    BrandStatus status;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Product> products = new HashSet<>();

}
