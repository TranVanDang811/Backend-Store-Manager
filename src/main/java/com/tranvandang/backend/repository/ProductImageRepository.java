package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage,String> {
}
