package com.tranvandang.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String phoneNumber;
    String receiverName;
    String street;
    String streetNumber;
    String district;
    String city;

    @Column(nullable = false)
    boolean isDefault = false;

    @ManyToOne
    @JsonIgnore
    User user;
}