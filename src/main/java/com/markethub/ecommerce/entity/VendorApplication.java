package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "vendor_applications") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VendorApplication {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String name;
    private String email;
    private String phone;
    private String storeName;
    private String category;
    @Column(columnDefinition = "TEXT") private String description;
    @Enumerated(EnumType.STRING) @Builder.Default private AppStatus status = AppStatus.PENDING;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    public enum AppStatus { PENDING, APPROVED, REJECTED }
}
