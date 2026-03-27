package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "wishlist_items", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "productId"}))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WishlistItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String userId;
    private String productId;
}
