package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.ImportOrder;
import com.tranvandang.backend.util.ImportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportOrderRepository extends JpaRepository<ImportOrder,String> {
    Page<ImportOrder> findByStatus(ImportStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "importDetails")
    @Query("SELECT o FROM ImportOrder o WHERE o.id = :id")
    Optional<ImportOrder> findByIdWithDetails(@Param("id") String id);

}
