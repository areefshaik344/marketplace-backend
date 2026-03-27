package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "addresses") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String userId;
    private String name;
    private String phone;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    @Enumerated(EnumType.STRING) @Builder.Default private Label label = Label.HOME;
    @Builder.Default private boolean isDefault = false;

    public enum Label { HOME, OFFICE, WORK, OTHER }
}
