package com.tranvandang.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
    @JsonProperty("pending")
    PENDING,

    @JsonProperty("processing")
    PROCESSING,


    @JsonProperty("delivered")
    DELIVERED,

    @JsonProperty("canceled")
    CANCELED,
}
