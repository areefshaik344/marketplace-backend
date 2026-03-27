package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity @Table(name = "vendor_shipping_settings") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VendorShippingSettings {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    @Column(unique = true) private String vendorId;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private Map<String, Object> settings;
}
