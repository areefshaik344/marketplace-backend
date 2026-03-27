package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity @Table(name = "platform_settings") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PlatformSettings {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private Map<String, Object> settings;
}
