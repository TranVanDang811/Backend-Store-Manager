package com.tranvandang.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    String phoneNumber;
    String receiverName;
    String street;
    String streetNumber;
    String district;
    String city;
    boolean isDefault;
}
