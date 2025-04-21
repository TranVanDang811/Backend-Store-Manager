package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage,String> {
    List<ProductImage> findByProductId(String productId);
    Optional<ProductImage> findByImageUrl(String imageUrl);

}
