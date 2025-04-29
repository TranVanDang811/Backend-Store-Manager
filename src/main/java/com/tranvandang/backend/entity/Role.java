package com.tranvandang.backend.entity;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role{
    @Id
    String name;

    @Column(name = "description")
    String description;
    @ManyToMany
    Set<Permission> permissions;

}