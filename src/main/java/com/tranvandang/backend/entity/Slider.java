package com.tranvandang.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tranvandang.backend.util.ChangerStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Slider extends AbstractEntity {


    String title;
    String description;
    String imageUrl;
    @OneToOne(mappedBy = "slider", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Images images;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ChangerStatus status;
}
