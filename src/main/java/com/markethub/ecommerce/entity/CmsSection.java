package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity @Table(name = "cms_sections") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CmsSection {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String key;
    private String title;
    private String type;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private Map<String, Object> config;
    @Builder.Default private int sortOrder = 0;
    @Builder.Default private boolean active = true;
}
