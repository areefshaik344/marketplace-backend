package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name = "orders") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String userId;
    @Enumerated(EnumType.STRING) @Builder.Default private OrderStatus status = OrderStatus.PLACED;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private List<Map<String, Object>> items;
    private double total;
    @Builder.Default private double tax = 0;
    @Builder.Default private double shipping = 0;
    @Builder.Default private double discount = 0;
    private double grandTotal;
    private String paymentMethod;
    private String shippingAddressId;
    private String couponCode;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private List<Map<String, Object>> timeline;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();

    public enum OrderStatus { PLACED, CONFIRMED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, RETURN_REQUESTED, REFUNDED }
}
