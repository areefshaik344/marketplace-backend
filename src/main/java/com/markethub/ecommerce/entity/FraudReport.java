package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "fraud_reports") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FraudReport {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String type;
    private String referenceId;
    @Column(columnDefinition = "TEXT") private String reason;
    @Enumerated(EnumType.STRING) @Builder.Default private FraudStatus status = FraudStatus.OPEN;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    public enum FraudStatus { OPEN, INVESTIGATING, RESOLVED, DISMISSED }
}
