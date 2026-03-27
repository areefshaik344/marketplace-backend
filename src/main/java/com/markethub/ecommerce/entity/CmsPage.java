package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "cms_pages") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CmsPage {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String title;
    private String slug;
    @Column(columnDefinition = "TEXT") private String content;
    @Builder.Default private boolean published = true;
}
