package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "email_templates") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmailTemplate {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String name;
    private String subject;
    @Column(columnDefinition = "TEXT") private String body;
    @Builder.Default private boolean active = true;
}
