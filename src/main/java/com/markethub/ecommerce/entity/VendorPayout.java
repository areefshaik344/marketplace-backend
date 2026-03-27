package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "vendor_payouts") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VendorPayout {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String vendorId;
    private double amount;
    @Enumerated(EnumType.STRING) @Builder.Default private PayoutStatus status = PayoutStatus.PENDING;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    public enum PayoutStatus { PENDING, COMPLETED }
}
