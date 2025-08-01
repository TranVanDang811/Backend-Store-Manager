package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  SupplierRepository extends JpaRepository<Supplier,String> {
    Optional<Supplier> findById(String id);
    Page<Supplier> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    boolean existsByNameIgnoreCase(String name);

}
