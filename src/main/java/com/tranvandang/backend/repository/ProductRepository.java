package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.Brand;
import com.tranvandang.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,String> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
    List<Product> findByBrand_Name(String brandName);
    List<Product> findByCategory_Name(String categoryName);

}
