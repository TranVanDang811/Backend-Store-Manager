package com.tranvandang.backend.entity;

import com.tranvandang.backend.util.Gender;
import com.tranvandang.backend.util.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User extends AbstractEntity {

    String firstName;
    String lastName;
    Gender gender;
    String email;
    String phone;
    LocalDate dob;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    UserStatus status;

    @ManyToMany
    Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Address> addresses;

}
