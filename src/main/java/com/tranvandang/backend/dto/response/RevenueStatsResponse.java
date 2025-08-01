package com.tranvandang.backend.dto.response;

import java.math.BigDecimal;

public record RevenueStatsResponse(
        Long totalOrders,
        BigDecimal totalRevenue,
        Integer totalProductsSold
) {}