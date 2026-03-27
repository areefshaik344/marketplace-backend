package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "categories") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String name;
    private String slug;
    private String image;
    private String parentId;
    @Builder.Default private int sortOrder = 0;
    @Builder.Default private boolean active = true;
}
