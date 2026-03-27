package com.markethub.ecommerce.service;
import com.markethub.ecommerce.entity.*;
import com.markethub.ecommerce.exception.AppException;
import com.markethub.ecommerce.repository.*;
import com.markethub.ecommerce.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service @RequiredArgsConstructor
public class OtpService {
    private final OtpTokenRepository otpRepo;
    private final UserRepository userRepo;
    private final JwtProvider jwt;
    private final PasswordEncoder encoder;
    private final AuthService authService;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;

    @Transactional
    public Map<String, Object> sendOtp(String phone) {
        // Rate limit: delete old OTP if exists
        otpRepo.findByPhone(phone).ifPresent(otpRepo::delete);

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        otpRepo.save(OtpToken.builder()
            .phone(phone).otp(otp).attempts(0)
            .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
            .build());

        // In production, send SMS here (Twilio, etc.)
        // For dev/testing, return the OTP in the response
        return Map.of("message", "OTP sent successfully", "otp", otp);
    }

    @Transactional
    public Map<String, Object> verifyOtp(String phone, String otp) {
        OtpToken stored = otpRepo.findByPhone(phone)
            .orElseThrow(() -> AppException.badRequest("No OTP found. Please request a new one."));

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepo.delete(stored);
            throw AppException.badRequest("OTP expired. Please request a new one.");
        }

        if (stored.getAttempts() >= MAX_ATTEMPTS) {
            otpRepo.delete(stored);
            throw AppException.badRequest("Too many failed attempts. Please request a new OTP.");
        }

        if (!stored.getOtp().equals(otp)) {
            stored.setAttempts(stored.getAttempts() + 1);
            otpRepo.save(stored);
            throw AppException.unauthorized("Invalid OTP. " + (MAX_ATTEMPTS - stored.getAttempts()) + " attempts remaining.");
        }

        // OTP verified — clean up
        otpRepo.delete(stored);

        // Find or create user by phone
        User user = userRepo.findByPhone(phone).orElseGet(() -> {
            User newUser = User.builder()
                .name("User " + phone.substring(phone.length() - 4))
                .email(phone + "@phone.markethub.com")
                .phone(phone)
                .password(encoder.encode(UUID.randomUUID().toString()))
                .role(User.Role.CUSTOMER)
                .emailVerified(true)
                .build();
            return userRepo.save(newUser);
        });

        String accessToken = jwt.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwt.generateRefreshToken(user.getId());

        return Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken,
            "expiresIn", 900,
            "user", authService.mapUser(user)
        );
    }
}
