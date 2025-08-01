package com.tranvandang.backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressAddRequest {
    String phoneNumber;
    String receiverName;
    String street;
    String streetNumber;
    String district;
    String city;
    boolean isDefault;
    String userId;
}
