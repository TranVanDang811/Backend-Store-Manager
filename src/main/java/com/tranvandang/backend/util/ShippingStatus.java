package com.tranvandang.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShippingStatus {
    @JsonProperty("pending")
    PENDING,     // Chờ xử lý

    @JsonProperty("shipped")
    SHIPPED,     // Đã giao cho đơn vị vận chuyển

    @JsonProperty("in_transit")
    IN_TRANSIT,  // Đang trên đường

    @JsonProperty("delivered")
    DELIVERED,   // Đã nhận hàng

    @JsonProperty("returned")
    RETURNED     // Bị trả lại
}
