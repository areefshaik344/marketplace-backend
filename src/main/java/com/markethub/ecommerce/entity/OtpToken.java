package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "otp_tokens") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OtpToken {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    @Column(unique = true) private String phone;
    private String otp;
    private int attempts;
    private LocalDateTime expiresAt;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
}
