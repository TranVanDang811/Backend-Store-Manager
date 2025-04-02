package com.tranvandang.backend.repository;


import com.tranvandang.backend.entity.Product;
import com.tranvandang.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    Page<User> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    Optional<User> findByUsername(String username);
}
