package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "coupons") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Coupon {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    @Column(unique = true) private String code;
    private String description;
    @Enumerated(EnumType.STRING) private DiscountType discountType;
    private double discountValue;
    private double minOrder;
    private Double maxDiscount;
    private String label;
    private String vendorId;
    @Builder.Default private boolean active = true;
    private LocalDateTime expiresAt;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    public enum DiscountType { PERCENT, FLAT }
}
