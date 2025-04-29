package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductImageRepository extends JpaRepository<ProductImage,String> {
    List<ProductImage> findByProductId(String productId);


}
