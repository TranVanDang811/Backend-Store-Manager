package com.tranvandang.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String imageUrl;  // Đường dẫn ảnh

    String publicId;
    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;


    @OneToOne(optional = true)
    @JoinColumn(name = "slider_id", nullable = true)
    Slider slider;

    @OneToOne(optional = true)
    @JoinColumn(name = "brand_id", nullable = true)
    Brand brand;}
