package com.tranvandang.backend.repository;


import com.tranvandang.backend.entity.Cart;
import com.tranvandang.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
    Optional<Cart> findByUser(User user);
}
