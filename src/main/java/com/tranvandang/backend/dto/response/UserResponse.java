package com.tranvandang.backend.dto.response;

import com.tranvandang.backend.util.Gender;
import com.tranvandang.backend.util.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String firstName;
    String lastName;
    String username;
    Gender gender;
    String email;
    String phone;
    UserStatus status;
    LocalDate dob;


    Set<RoleResponse> roles;
    Set<AddressResponse> addresses;
}
