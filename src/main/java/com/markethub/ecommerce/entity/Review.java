package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "reviews") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String productId;
    private String userId;
    private String userName;
    private int rating;
    private String title;
    @Column(columnDefinition = "TEXT") private String comment;
    @Builder.Default private int helpful = 0;
    @Builder.Default private boolean verified = false;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
}
