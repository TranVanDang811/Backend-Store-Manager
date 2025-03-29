package com.tranvandang.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShippingMethod {
    @JsonProperty("standard")
    STANDARD,  // Giao hàng tiêu chuẩn

    @JsonProperty("express")
    EXPRESS,   // Giao hàng nhanh

    @JsonProperty("same_day")
    SAME_DAY,  // Giao hàng trong ngày

    @JsonProperty("international")
    INTERNATIONAL // Giao hàng quốc tế
}
