package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "refresh_tokens") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String userId;
    @Column(length = 512) private String tokenHash;
    private String deviceInfo;
    private LocalDateTime expiresAt;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
}
