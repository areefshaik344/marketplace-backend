package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "audit_log") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String action;
    private String userId;
    private String userName;
    @Column(columnDefinition = "TEXT") private String details;
    @Enumerated(EnumType.STRING) @Builder.Default private Severity severity = Severity.INFO;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    public enum Severity { INFO, WARNING, ERROR }
}
