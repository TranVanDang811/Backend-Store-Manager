package com.tranvandang.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardStatsResponse {
    Long totalOrders;
    Long pendingOrders;
    Long deliveredOrders;
    Long canceledOrders;
    BigDecimal totalRevenue;
    Integer totalProductsSold;
}
