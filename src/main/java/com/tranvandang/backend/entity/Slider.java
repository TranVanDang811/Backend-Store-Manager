package com.tranvandang.backend.entity;

import com.tranvandang.backend.util.ChangerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Slider extends AbstractEntity {

    String imageUrl;
    String publicId;
    String title;
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ChangerStatus status;
}
