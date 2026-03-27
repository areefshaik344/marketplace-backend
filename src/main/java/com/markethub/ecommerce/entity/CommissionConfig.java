package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "commission_config") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CommissionConfig {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String category;
    private String vendorId;
    private double rate;
    @Builder.Default private boolean isOverride = false;
}
