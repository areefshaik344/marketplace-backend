package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "vendors") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Vendor {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String userId;
    private String storeName;
    @Column(columnDefinition = "TEXT") private String description;
    private String logo;
    private String banner;
    private String category;
    @Builder.Default private double rating = 0;
    @Builder.Default private int totalProducts = 0;
    @Builder.Default private int totalOrders = 0;
    @Enumerated(EnumType.STRING) @Builder.Default private VendorStatus status = VendorStatus.PENDING;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    public enum VendorStatus { PENDING, ACTIVE, SUSPENDED }
}
