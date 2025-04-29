package com.tranvandang.backend.entity;

import com.tranvandang.backend.util.ImportStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ImportOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    LocalDateTime importDate;

    String note;

    @Enumerated(EnumType.STRING)
    ImportStatus status;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    Supplier supplier;

    @OneToMany(mappedBy = "importOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<ImportOrderDetail> importDetails = new ArrayList<>();

    BigDecimal totalPrice;
}
