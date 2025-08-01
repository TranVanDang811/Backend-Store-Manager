package com.tranvandang.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BrandStatus {
    @JsonProperty("active")
    ACTIVE,

    @JsonProperty("inactive")
    INACTIVE,

    @JsonProperty("none")
    NONE
}
