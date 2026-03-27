package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "banners") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Banner {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String title;
    private String subtitle;
    private String image;
    private String link;
    @Builder.Default private int sortOrder = 0;
    @Builder.Default private boolean active = true;
}
