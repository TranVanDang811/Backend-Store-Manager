package com.tranvandang.backend.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressUpdateRequest {
    String id;
    String phoneNumber;
    String receiverName;
    String street;
    String streetNumber;
    String district;
    String city;
}
