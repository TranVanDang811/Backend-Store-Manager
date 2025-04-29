package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {
    Optional<Discount> findByCode(String code);

}
