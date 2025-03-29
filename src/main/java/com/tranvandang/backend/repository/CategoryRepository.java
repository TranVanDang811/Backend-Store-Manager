package com.tranvandang.backend.repository;


import com.tranvandang.backend.entity.Brand;
import com.tranvandang.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByName(String name);

}
