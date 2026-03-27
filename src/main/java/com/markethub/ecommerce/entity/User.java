package com.markethub.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
    private String name;
    @Column(unique = true) private String email;
    private String phone;
    private String password;
    private String avatar;
    @Enumerated(EnumType.STRING) private Role role;
    @Builder.Default private boolean active = true;
    @Builder.Default private int failedLoginAttempts = 0;
    private LocalDateTime lockedUntil;
    private String emailVerificationToken;
    @Builder.Default private boolean emailVerified = false;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordExpiry;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    public enum Role { CUSTOMER, VENDOR, ADMIN }
}
