package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.Orders;
import com.tranvandang.backend.util.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders,String > {
    List<Orders> findByUserId(String userId);
    List<Orders> findByStatus(OrderStatus status);
    List<Orders> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
