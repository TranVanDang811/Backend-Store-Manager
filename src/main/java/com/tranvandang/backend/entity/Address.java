package com.tranvandang.backend.entity;


import java.util.UUID;

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
    String street;
    String streetNumber;
    String district;
    String city;

    @ManyToOne
    @JsonIgnore
    User user;
}