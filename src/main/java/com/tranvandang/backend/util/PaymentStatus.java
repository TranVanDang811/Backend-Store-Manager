package com.tranvandang.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentStatus {
    @JsonProperty("pending")
    PENDING,      // Chờ thanh toán

    @JsonProperty("completed")
    COMPLETED,    // Thanh toán đã hoàn tất, đơn hàng đang được xử lý

    @JsonProperty("failed")
    FAILED,       // Thanh toán thất bại

    @JsonProperty("canceled")
    CANCELED      // Hủy đơn (COD khách không nhận hàng)
}
