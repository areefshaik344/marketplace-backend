package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.ApiResponse;
import com.markethub.ecommerce.service.AuthService;
import com.markethub.ecommerce.service.OtpService;
import com.markethub.ecommerce.repository.UserRepository;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepo;
    private final OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletResponse res) {
        var result = authService.login(body.get("email"), body.get("password"), "web");
        addRefreshCookie(res, (String) result.get("refreshToken"));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body, HttpServletResponse res) {
        var result = authService.register(body.get("name"), body.get("email"), body.get("phone"), body.get("password"));
        addRefreshCookie(res, (String) result.get("refreshToken"));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken", required = false) String cookie, @RequestBody(required = false) Map<String, String> body, HttpServletResponse res) {
        String token = cookie != null ? cookie : (body != null ? body.get("refreshToken") : null);
        if (token == null) return ResponseEntity.status(401).body(ApiResponse.error("No refresh token"));
        var result = authService.refresh(token);
        addRefreshCookie(res, (String) result.get("refreshToken"));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String cookie, HttpServletResponse res) {
        if (cookie != null) authService.logout(cookie);
        clearRefreshCookie(res);
        return ResponseEntity.ok(ApiResponse.ok(null, "Logged out"));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(HttpServletResponse res) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        authService.logoutAll(userId);
        clearRefreshCookie(res);
        return ResponseEntity.ok(ApiResponse.ok(null, "All sessions revoked"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        authService.forgotPassword(body.get("email"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Reset link sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token") != null ? body.get("token") : body.get("code");
        authService.resetPassword(token, body.get("password") != null ? body.get("password") : body.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Password reset"));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> body) {
        String code = body.get("code") != null ? body.get("code") : body.get("token");
        authService.verifyEmail(code);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("verified", true)));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var user = userRepo.findById(userId).orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok(authService.mapUser(user)));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> body) {
        var result = otpService.sendOtp(body.get("phone"));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body, HttpServletResponse res) {
        var result = otpService.verifyOtp(body.get("phone"), body.get("otp"));
        if (result.containsKey("refreshToken")) addRefreshCookie(res, (String) result.get("refreshToken"));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    private void addRefreshCookie(HttpServletResponse res, String token) {
        var c = new Cookie("refreshToken", token); c.setHttpOnly(true); c.setSecure(true); c.setPath("/api/v1/auth"); c.setMaxAge(7*24*3600); res.addCookie(c);
    }
    private void clearRefreshCookie(HttpServletResponse res) {
        var c = new Cookie("refreshToken", ""); c.setHttpOnly(true); c.setPath("/api/v1/auth"); c.setMaxAge(0); res.addCookie(c);
    }
}
