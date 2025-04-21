package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.Brand;
import com.tranvandang.backend.entity.Category;
import com.tranvandang.backend.entity.Product;
import com.tranvandang.backend.util.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface    ProductRepository extends JpaRepository<Product,String> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
    List<Product> findByBrand_Name(String brandName);
    List<Product> findByCategory_Name(String categoryName);
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByCategory_NameAndStatus(String categoryName, ProductStatus status, Pageable pageable);

    Page<Product> findByBrand_NameAndStatus(String brandName, ProductStatus status, Pageable pageable);

    Page<Product> findByCategory_NameAndBrand_NameAndStatus(String categoryName, String brandName, ProductStatus status, Pageable pageable);

    Page<Product> findByCategory_Name(String categoryName, Pageable pageable);

    Page<Product> findByBrand_Name(String brandName, Pageable pageable);

    Page<Product> findByCategory_NameAndBrand_Name(String categoryName, String brandName, Pageable pageable);

    List<Product> findTop5ByCategoryAndIdNot(Category category, String excludedProductId);

    long countByStatus(ProductStatus status);

}
