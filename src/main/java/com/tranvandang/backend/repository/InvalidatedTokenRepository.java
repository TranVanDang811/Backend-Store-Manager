package com.tranvandang.backend.repository;


import com.tranvandang.backend.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    boolean existsById(String id);

    int deleteAllByExpiryTimeBefore(Date now);
}
