package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "cart_items") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String userId;
    private String productId;
    private String productName;
    private String productImage;
    private String vendorId;
    private String vendorName;
    private double price;
    private double originalPrice;
    private int quantity;
    private int stock;
    private String variant;
}
