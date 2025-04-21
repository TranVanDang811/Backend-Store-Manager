package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    void deleteAllByProductId(String productId);
}
