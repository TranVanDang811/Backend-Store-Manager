package com.tranvandang.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ImportStatus {
    @JsonProperty("not confirmed")
    NOT_CONFIRMED,

    @JsonProperty("imported")
    IMPORTED,

    @JsonProperty("cancelled")
    CANCELLED
}
