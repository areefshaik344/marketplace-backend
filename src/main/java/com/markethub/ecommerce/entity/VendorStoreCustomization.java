package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity @Table(name = "vendor_store_customization") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VendorStoreCustomization {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    @Column(unique = true) private String vendorId;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private Map<String, Object> customization;
}
