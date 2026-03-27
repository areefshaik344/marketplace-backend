package com.markethub.ecommerce.service;
import com.markethub.ecommerce.entity.*;
import com.markethub.ecommerce.exception.AppException;
import com.markethub.ecommerce.repository.*;
import com.markethub.ecommerce.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final VendorRepository vendorRepo;
    private final JwtProvider jwt;
    private final PasswordEncoder encoder;
    private final AuditService audit;
    @Value("${app.max-login-attempts}") private int maxAttempts;
    @Value("${app.lock-duration-minutes}") private int lockMinutes;

    public Map<String, Object> login(String email, String password, String device) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> AppException.unauthorized("Invalid credentials"));
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now()))
            throw AppException.unauthorized("Account locked. Try again later.");
        if (!encoder.matches(password, user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= maxAttempts) user.setLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
            userRepo.save(user);
            throw AppException.unauthorized("Invalid credentials");
        }
        user.setFailedLoginAttempts(0); user.setLockedUntil(null); userRepo.save(user);
        String access = jwt.generateAccessToken(user.getId(), user.getRole().name());
        String refresh = jwt.generateRefreshToken(user.getId());
        saveRefreshToken(user.getId(), refresh, device);
        audit.log("LOGIN", user.getId(), "User logged in");
        return Map.of("accessToken", access, "refreshToken", refresh, "expiresIn", 900, "user", mapUser(user));
    }

    @Transactional
    public Map<String, Object> register(String name, String email, String phone, String password) {
        if (userRepo.findByEmail(email).isPresent()) throw AppException.conflict("Email already registered");
        User user = User.builder().name(name).email(email).phone(phone).password(encoder.encode(password))
            .role(User.Role.CUSTOMER).emailVerificationToken(UUID.randomUUID().toString()).build();
        userRepo.save(user);
        String access = jwt.generateAccessToken(user.getId(), user.getRole().name());
        String refresh = jwt.generateRefreshToken(user.getId());
        saveRefreshToken(user.getId(), refresh, "web");
        audit.log("REGISTER", user.getId(), "New user registered");
        return Map.of("accessToken", access, "refreshToken", refresh, "user", mapUser(user));
    }

    public Map<String, Object> refresh(String refreshToken) {
        if (!jwt.isValid(refreshToken)) throw AppException.unauthorized("Invalid refresh token");
        String hash = hashToken(refreshToken);
        RefreshToken stored = refreshRepo.findByTokenHash(hash).orElseThrow(() -> AppException.unauthorized("Token revoked"));
        String userId = jwt.getUserId(refreshToken);
        User user = userRepo.findById(userId).orElseThrow(() -> AppException.notFound("User not found"));
        // Rotate token
        refreshRepo.delete(stored);
        String newAccess = jwt.generateAccessToken(userId, user.getRole().name());
        String newRefresh = jwt.generateRefreshToken(userId);
        saveRefreshToken(userId, newRefresh, stored.getDeviceInfo());
        return Map.of("accessToken", newAccess, "refreshToken", newRefresh, "expiresIn", 900, "user", mapUser(user));
    }

    @Transactional public void logout(String refreshToken) { refreshRepo.deleteByTokenHash(hashToken(refreshToken)); }
    @Transactional public void logoutAll(String userId) { refreshRepo.deleteByUserId(userId); }

    public void forgotPassword(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> AppException.notFound("Email not found"));
        user.setResetPasswordToken(UUID.randomUUID().toString());
        user.setResetPasswordExpiry(LocalDateTime.now().plusHours(1));
        userRepo.save(user);
        // TODO: send email
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepo.findByResetPasswordToken(token).orElseThrow(() -> AppException.badRequest("Invalid token"));
        if (user.getResetPasswordExpiry().isBefore(LocalDateTime.now())) throw AppException.badRequest("Token expired");
        user.setPassword(encoder.encode(newPassword)); user.setResetPasswordToken(null); user.setResetPasswordExpiry(null);
        userRepo.save(user);
    }

    public void verifyEmail(String code) {
        User user = userRepo.findByEmailVerificationToken(code).orElseThrow(() -> AppException.badRequest("Invalid code"));
        user.setEmailVerified(true); user.setEmailVerificationToken(null); userRepo.save(user);
    }

    public Map<String, Object> mapUser(User u) {
        return Map.of("id", u.getId(), "name", u.getName(), "email", u.getEmail(),
            "phone", u.getPhone() != null ? u.getPhone() : "", "avatar", u.getAvatar() != null ? u.getAvatar() : "",
            "role", u.getRole().name().toLowerCase(), "joinedDate", u.getCreatedAt().toString());
    }

    private void saveRefreshToken(String userId, String token, String device) {
        refreshRepo.save(RefreshToken.builder().userId(userId).tokenHash(hashToken(token))
            .deviceInfo(device).expiresAt(LocalDateTime.now().plusDays(7)).build());
    }

    private String hashToken(String token) {
        try { return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
