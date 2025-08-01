package com.tranvandang.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentMethod {

    @JsonProperty("cash_on_delivery")
    CASH_ON_DELIVERY,

    @JsonProperty("bank_transfer")
    BANK_TRANSFER
}
