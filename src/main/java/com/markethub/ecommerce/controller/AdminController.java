package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.ApiResponse;
import com.markethub.ecommerce.entity.*;
import com.markethub.ecommerce.exception.AppException;
import com.markethub.ecommerce.repository.*;
import com.markethub.ecommerce.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/admin") @RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepo;
    private final VendorRepository vendorRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final CouponRepository couponRepo;
    private final CategoryRepository categoryRepo;
    private final BannerRepository bannerRepo;
    private final CmsSectionRepository sectionRepo;
    private final CmsPageRepository pageRepo;
    private final CommissionConfigRepository commissionRepo;
    private final AuditLogRepository auditRepo;
    private final VendorApplicationRepository appRepo;
    private final PlatformSettingsRepository settingsRepo;
    private final EmailTemplateRepository emailRepo;
    private final FraudReportRepository fraudRepo;
    private final AuditService audit;

    private String uid() { return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(); }

    // ── Users ──
    @GetMapping("/users") public ResponseEntity<?> users() { return ResponseEntity.ok(ApiResponse.ok(userRepo.findAll())); }
    @GetMapping("/users/{id}") public ResponseEntity<?> user(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(userRepo.findById(id).orElseThrow(() -> AppException.notFound("User not found"))));
    }
    @PatchMapping("/users/{id}/toggle-active") public ResponseEntity<?> toggleUser(@PathVariable String id) {
        var u = userRepo.findById(id).orElseThrow(() -> AppException.notFound("User not found"));
        u.setActive(!u.isActive()); userRepo.save(u);
        audit.log("USER_TOGGLE", uid(), "Toggled user " + id);
        return ResponseEntity.ok(ApiResponse.ok(u));
    }
    @DeleteMapping("/users/{id}") public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userRepo.deleteById(id); return ResponseEntity.ok(ApiResponse.ok(null, "Deleted"));
    }

    // ── Vendors ──
    @GetMapping("/vendors") public ResponseEntity<?> vendors() { return ResponseEntity.ok(ApiResponse.ok(vendorRepo.findAll())); }
    @GetMapping("/vendors/{id}") public ResponseEntity<?> vendor(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(vendorRepo.findById(id).orElseThrow(() -> AppException.notFound("Vendor not found"))));
    }
    @PatchMapping("/vendors/{id}/approve") public ResponseEntity<?> approveVendor(@PathVariable String id) {
        var v = vendorRepo.findById(id).orElseThrow(() -> AppException.notFound("Vendor not found"));
        v.setStatus(Vendor.VendorStatus.ACTIVE); vendorRepo.save(v);
        audit.log("VENDOR_APPROVED", uid(), "Approved vendor " + id);
        return ResponseEntity.ok(ApiResponse.ok(v));
    }
    @PatchMapping("/vendors/{id}/reject") public ResponseEntity<?> rejectVendor(@PathVariable String id) {
        var v = vendorRepo.findById(id).orElseThrow(() -> AppException.notFound("Vendor not found"));
        v.setStatus(Vendor.VendorStatus.SUSPENDED); vendorRepo.save(v);
        return ResponseEntity.ok(ApiResponse.ok(v));
    }

    // ── Products ──
    @GetMapping("/products") public ResponseEntity<?> products() { return ResponseEntity.ok(ApiResponse.ok(productRepo.findAll())); }
    @GetMapping("/products/{id}") public ResponseEntity<?> product(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(productRepo.findById(id).orElseThrow(() -> AppException.notFound("Product not found"))));
    }

    // ── Orders ──
    @GetMapping("/orders") public ResponseEntity<?> orders() { return ResponseEntity.ok(ApiResponse.ok(orderRepo.findAllByOrderByCreatedAtDesc())); }
    @GetMapping("/orders/{id}") public ResponseEntity<?> order(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(orderRepo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"))));
    }
    @PatchMapping("/orders/{id}/status") public ResponseEntity<?> orderStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        var order = orderRepo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"));
        order.setStatus(Order.OrderStatus.valueOf(body.get("status").toUpperCase()));
        return ResponseEntity.ok(ApiResponse.ok(orderRepo.save(order)));
    }
    @PostMapping("/orders/{id}/refund") public ResponseEntity<?> refund(@PathVariable String id, @RequestBody Map<String, Object> body) {
        var order = orderRepo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"));
        order.setStatus(Order.OrderStatus.REFUNDED);
        audit.log("ORDER_REFUND", uid(), "Refunded order " + id);
        return ResponseEntity.ok(ApiResponse.ok(orderRepo.save(order)));
    }

    // ── Analytics & Reporting ──
    @GetMapping("/analytics") public ResponseEntity<?> analytics() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("totalUsers", userRepo.count(), "totalVendors", vendorRepo.count(),
            "totalProducts", productRepo.count(), "totalOrders", orderRepo.count(), "totalRevenue", 0, "monthlyRevenue", List.of(), "ordersByStatus", Map.of())));
    }
    @GetMapping("/reporting") public ResponseEntity<?> reporting() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("totalUsers", userRepo.count(), "totalOrders", orderRepo.count())));
    }

    // ── Coupons ──
    @GetMapping("/coupons") public ResponseEntity<?> coupons() { return ResponseEntity.ok(ApiResponse.ok(couponRepo.findAll())); }
    @PostMapping("/coupons") public ResponseEntity<?> createCoupon(@RequestBody Map<String, Object> body) {
        var c = Coupon.builder().code((String) body.get("code"))
            .discountType(Coupon.DiscountType.valueOf(body.getOrDefault("discountType", "PERCENT").toString().toUpperCase()))
            .discountValue(Double.parseDouble(body.getOrDefault("discountValue", 0).toString()))
            .minOrder(Double.parseDouble(body.getOrDefault("minOrder", 0).toString()))
            .label((String) body.getOrDefault("label", "")).description((String) body.getOrDefault("description", "")).build();
        return ResponseEntity.ok(ApiResponse.ok(couponRepo.save(c)));
    }
    @PatchMapping("/coupons/{id}/toggle") public ResponseEntity<?> toggleCoupon(@PathVariable String id) {
        var c = couponRepo.findById(id).orElseThrow(() -> AppException.notFound("Coupon not found"));
        c.setActive(!c.isActive()); return ResponseEntity.ok(ApiResponse.ok(couponRepo.save(c)));
    }
    @DeleteMapping("/coupons/{id}") public ResponseEntity<?> deleteCoupon(@PathVariable String id) {
        couponRepo.deleteById(id); return ResponseEntity.ok(ApiResponse.ok(null, "Deleted"));
    }

    // ── Categories ──
    @GetMapping("/categories") public ResponseEntity<?> categories() { return ResponseEntity.ok(ApiResponse.ok(categoryRepo.findAll())); }
    @PostMapping("/categories") public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> body) {
        var c = Category.builder().name((String) body.get("name")).slug(((String) body.get("name")).toLowerCase().replaceAll("[^a-z0-9]+", "-"))
            .image((String) body.get("image")).parentId((String) body.get("parentId")).build();
        return ResponseEntity.ok(ApiResponse.ok(categoryRepo.save(c)));
    }
    @PutMapping("/categories/{id}") public ResponseEntity<?> updateCategory(@PathVariable String id, @RequestBody Map<String, Object> body) {
        var c = categoryRepo.findById(id).orElseThrow(() -> AppException.notFound("Category not found"));
        if (body.containsKey("name")) c.setName((String) body.get("name"));
        if (body.containsKey("image")) c.setImage((String) body.get("image"));
        return ResponseEntity.ok(ApiResponse.ok(categoryRepo.save(c)));
    }
    @DeleteMapping("/categories/{id}") public ResponseEntity<?> deleteCategory(@PathVariable String id) {
        categoryRepo.deleteById(id); return ResponseEntity.ok(ApiResponse.ok(null, "Deleted"));
    }

    // ── Audit Log ──
    @GetMapping("/audit-log") public ResponseEntity<?> auditLog() { return ResponseEntity.ok(ApiResponse.ok(auditRepo.findAllByOrderByCreatedAtDesc())); }

    // ── Vendor Applications ──
    @GetMapping("/vendor-applications") public ResponseEntity<?> applications() { return ResponseEntity.ok(ApiResponse.ok(appRepo.findAll())); }
    @PatchMapping("/vendor-applications/{id}/approve") public ResponseEntity<?> approveApp(@PathVariable String id) {
        var app = appRepo.findById(id).orElseThrow(() -> AppException.notFound("Application not found"));
        app.setStatus(VendorApplication.AppStatus.APPROVED); appRepo.save(app);
        return ResponseEntity.ok(ApiResponse.ok(app));
    }
    @PatchMapping("/vendor-applications/{id}/reject") public ResponseEntity<?> rejectApp(@PathVariable String id) {
        var app = appRepo.findById(id).orElseThrow(() -> AppException.notFound("Application not found"));
        app.setStatus(VendorApplication.AppStatus.REJECTED); appRepo.save(app);
        return ResponseEntity.ok(ApiResponse.ok(app));
    }

    // ── Settings ──
    @GetMapping("/settings") public ResponseEntity<?> settings() {
        var s = settingsRepo.findAll(); return ResponseEntity.ok(ApiResponse.ok(s.isEmpty() ? Map.of() : s.get(0)));
    }
    @PutMapping("/settings") public ResponseEntity<?> updateSettings(@RequestBody Map<String, Object> body) {
        var all = settingsRepo.findAll();
        PlatformSettings s; if (all.isEmpty()) { s = PlatformSettings.builder().settings(body).build(); } else { s = all.get(0); s.setSettings(body); }
        return ResponseEntity.ok(ApiResponse.ok(settingsRepo.save(s)));
    }

    // ── Banners ──
    @GetMapping("/banners") public ResponseEntity<?> banners() { return ResponseEntity.ok(ApiResponse.ok(bannerRepo.findAll())); }
    @GetMapping("/banners/{id}") public ResponseEntity<?> banner(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(bannerRepo.findById(id).orElseThrow(() -> AppException.notFound("Banner not found"))));
    }
    @PostMapping("/banners") public ResponseEntity<?> createBanner(@RequestBody Map<String, Object> body) {
        var b = Banner.builder().title((String) body.get("title")).subtitle((String) body.get("subtitle"))
            .image((String) body.get("image")).link((String) body.get("link")).build();
        return ResponseEntity.ok(ApiResponse.ok(bannerRepo.save(b)));
    }
    @PutMapping("/banners/{id}") public ResponseEntity<?> updateBanner(@PathVariable String id, @RequestBody Map<String, Object> body) {
        var b = bannerRepo.findById(id).orElseThrow(() -> AppException.notFound("Banner not found"));
        if (body.containsKey("title")) b.setTitle((String) body.get("title"));
        if (body.containsKey("subtitle")) b.setSubtitle((String) body.get("subtitle"));
        if (body.containsKey("image")) b.setImage((String) body.get("image"));
        if (body.containsKey("link")) b.setLink((String) body.get("link"));
        return ResponseEntity.ok(ApiResponse.ok(bannerRepo.save(b)));
    }
    @DeleteMapping("/banners/{id}") public ResponseEntity<?> deleteBanner(@PathVariable String id) {
        bannerRepo.deleteById(id); return ResponseEntity.ok(ApiResponse.ok(null, "Deleted"));
    }

    // ── CMS Sections ──
    @GetMapping("/sections") public ResponseEntity<?> sections() { return ResponseEntity.ok(ApiResponse.ok(sectionRepo.findAll())); }
    @PutMapping("/sections") public ResponseEntity<?> updateSections(@RequestBody List<Map<String, Object>> body) {
        // Replace all sections
        sectionRepo.deleteAll();
        body.forEach(s -> sectionRepo.save(CmsSection.builder().key((String) s.get("key")).title((String) s.get("title"))
            .type((String) s.get("type")).config((Map<String, Object>) s.get("config")).build()));
        return ResponseEntity.ok(ApiResponse.ok(sectionRepo.findAll()));
    }

    // ── CMS Pages ──
    @GetMapping("/pages") public ResponseEntity<?> pages() { return ResponseEntity.ok(ApiResponse.ok(pageRepo.findAll())); }
    @DeleteMapping("/pages/{id}") public ResponseEntity<?> deletePage(@PathVariable String id) {
        pageRepo.deleteById(id); return ResponseEntity.ok(ApiResponse.ok(null, "Deleted"));
    }

    // ── Commission ──
    @GetMapping("/commission") public ResponseEntity<?> commission() { return ResponseEntity.ok(ApiResponse.ok(commissionRepo.findAll())); }
    @PutMapping("/commission") public ResponseEntity<?> updateCommission(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok(body));
    }
    @GetMapping("/commission/overrides") public ResponseEntity<?> overrides() { return ResponseEntity.ok(ApiResponse.ok(commissionRepo.findByIsOverrideTrue())); }

    // ── Fraud ──
    @GetMapping("/fraud/orders") public ResponseEntity<?> fraudOrders() { return ResponseEntity.ok(ApiResponse.ok(fraudRepo.findByType("order"))); }
    @GetMapping("/fraud/reviews") public ResponseEntity<?> fraudReviews() { return ResponseEntity.ok(ApiResponse.ok(fraudRepo.findByType("review"))); }
    @GetMapping("/fraud/reports") public ResponseEntity<?> fraudReports() { return ResponseEntity.ok(ApiResponse.ok(fraudRepo.findAll())); }
    @PostMapping("/fraud/{id}/action") public ResponseEntity<?> fraudAction(@PathVariable String id, @RequestBody Map<String, String> body) {
        var r = fraudRepo.findById(id).orElseThrow(() -> AppException.notFound("Report not found"));
        r.setStatus(FraudReport.FraudStatus.valueOf(body.get("action").toUpperCase()));
        return ResponseEntity.ok(ApiResponse.ok(fraudRepo.save(r)));
    }

    // ── Email Templates ──
    @GetMapping("/email-templates") public ResponseEntity<?> emailTemplates() { return ResponseEntity.ok(ApiResponse.ok(emailRepo.findAll())); }
    @PutMapping("/email-templates/{id}") public ResponseEntity<?> updateTemplate(@PathVariable String id, @RequestBody Map<String, Object> body) {
        var t = emailRepo.findById(id).orElseThrow(() -> AppException.notFound("Template not found"));
        if (body.containsKey("subject")) t.setSubject((String) body.get("subject"));
        if (body.containsKey("body")) t.setBody((String) body.get("body"));
        return ResponseEntity.ok(ApiResponse.ok(emailRepo.save(t)));
    }
}
