package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name = "products") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String slug;
    private String name;
    @Column(columnDefinition = "TEXT") private String description;
    private double price;
    private double originalPrice;
    private int discount;
    private String category;
    private String subcategory;
    private String brand;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private List<String> images;
    @Builder.Default private double rating = 0;
    @Builder.Default private int reviewCount = 0;
    private String vendorId;
    private String vendorName;
    private int stock;
    @Enumerated(EnumType.STRING) @Builder.Default private StockStatus stockStatus = StockStatus.IN_STOCK;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private List<String> tags;
    @Builder.Default private boolean featured = false;
    @Builder.Default private boolean trending = false;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private Map<String, String> specifications;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    public enum StockStatus { IN_STOCK, LOW_STOCK, OUT_OF_STOCK }
}
