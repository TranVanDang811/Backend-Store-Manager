package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.Address;
import com.tranvandang.backend.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, String> {
}