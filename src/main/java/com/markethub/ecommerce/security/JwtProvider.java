package com.markethub.ecommerce.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtProvider {
    private final SecretKey key;
    @Value("${app.jwt.access-token-expiry}") private long accessExpiry;
    @Value("${app.jwt.refresh-token-expiry}") private long refreshExpiry;

    public JwtProvider(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String userId, String role) {
        return Jwts.builder().subject(userId).claim("role", role)
            .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + accessExpiry))
            .signWith(key).compact();
    }

    public String generateRefreshToken(String userId) {
        return Jwts.builder().subject(userId).id(UUID.randomUUID().toString())
            .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + refreshExpiry))
            .signWith(key).compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public boolean isValid(String token) {
        try { parseClaims(token); return true; } catch (Exception e) { return false; }
    }

    public String getUserId(String token) { return parseClaims(token).getSubject(); }
    public String getRole(String token) { return parseClaims(token).get("role", String.class); }
}
