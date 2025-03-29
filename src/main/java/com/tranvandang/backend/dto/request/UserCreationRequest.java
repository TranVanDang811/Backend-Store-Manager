package com.tranvandang.backend.dto.request;


import com.tranvandang.backend.util.Gender;
import com.tranvandang.backend.util.UserStatus;
import com.tranvandang.backend.validator.DobConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE) // gan lop
public class UserCreationRequest {
    @Size(min = 3, message = "FIRSTNAME_INVALID")
    String firstName;

    String lastName;

    @Size(min = 3, message = "USERNAME_INVALID")
    String username;
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    Gender gender;
    String phone;
    String email;
    UserStatus status;
    @DobConstraint(min = 16, message = "INVALID_DOB")
    LocalDate dob;

    Set<AddressAddRequest> addresses;

}
