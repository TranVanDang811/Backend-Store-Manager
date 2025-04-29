package com.tranvandang.backend.dto.response;

import com.tranvandang.backend.util.ImportStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportOrderResponse {
    String id;
    String note;
    ImportStatus status;
    LocalDateTime importDate;
    BigDecimal totalPrice;
    String supplierId;
    String supplierName;

    List<ImportOrderDetailResponse> importDetails;
}
