package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  SupplierRepository extends JpaRepository<Supplier,String> {
    Optional<Supplier> findById(String id);
}
