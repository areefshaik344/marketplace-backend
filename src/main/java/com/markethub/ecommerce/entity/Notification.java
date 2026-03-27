package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "notifications") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String userId;
    @Enumerated(EnumType.STRING) private NType type;
    private String title;
    @Column(columnDefinition = "TEXT") private String message;
    @Builder.Default private boolean read = false;
    private String actionUrl;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    public enum NType { ORDER, PROMO, SYSTEM, VENDOR, ALERT }
}
