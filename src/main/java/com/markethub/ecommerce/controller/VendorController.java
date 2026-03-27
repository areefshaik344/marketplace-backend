package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.*;
import com.markethub.ecommerce.entity.*;
import com.markethub.ecommerce.exception.AppException;
import com.markethub.ecommerce.repository.*;
import com.markethub.ecommerce.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.util.stream.Collectors;

@RestController @RequiredArgsConstructor
public class VendorController {
    private final VendorRepository vendorRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final CouponRepository couponRepo;
    private final VendorShippingSettingsRepository shippingRepo;
    private final VendorStoreCustomizationRepository customRepo;
    private final VendorPayoutRepository payoutRepo;
    private final AuditService audit;

    private String uid() { return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(); }
    private Vendor vendor() { return vendorRepo.findByUserId(uid()).orElseThrow(() -> AppException.notFound("Vendor not found")); }

    // ── Profile ──
    @GetMapping("/vendor/profile") public ResponseEntity<?> profile() { return ResponseEntity.ok(ApiResponse.ok(vendor())); }
    @PutMapping("/vendor/profile") public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> body) {
        var v = vendor();
        if (body.containsKey("storeName")) v.setStoreName((String) body.get("storeName"));
        if (body.containsKey("description")) v.setDescription((String) body.get("description"));
        if (body.containsKey("logo")) v.setLogo((String) body.get("logo"));
        if (body.containsKey("banner")) v.setBanner((String) body.get("banner"));
        return ResponseEntity.ok(ApiResponse.ok(vendorRepo.save(v)));
    }

    // ── Products ──
    @GetMapping("/vendor/products") public ResponseEntity<?> products() { return ResponseEntity.ok(ApiResponse.ok(productRepo.findByVendorId(vendor().getId()))); }
    @GetMapping("/vendor/products/{id}") public ResponseEntity<?> product(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(productRepo.findById(id).orElseThrow(() -> AppException.notFound("Product not found"))));
    }
    @PostMapping("/vendor/products") public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> body) {
        var v = vendor();
        var p = Product.builder().name((String) body.get("name")).description((String) body.get("description"))
            .price(Double.parseDouble(body.getOrDefault("price", 0).toString()))
            .originalPrice(Double.parseDouble(body.getOrDefault("originalPrice", 0).toString()))
            .category((String) body.get("category")).brand((String) body.get("brand"))
            .stock(Integer.parseInt(body.getOrDefault("stock", 0).toString()))
            .vendorId(v.getId()).vendorName(v.getStoreName())
            .slug(((String) body.get("name")).toLowerCase().replaceAll("[^a-z0-9]+", "-"))
            .images(body.get("images") instanceof List ? (List<String>) body.get("images") : List.of())
            .tags(body.get("tags") instanceof List ? (List<String>) body.get("tags") : List.of())
            .build();
        return ResponseEntity.ok(ApiResponse.ok(productRepo.save(p)));
    }
    @PutMapping("/vendor/products/{id}") public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Map<String, Object> body) {
        var p = productRepo.findById(id).orElseThrow(() -> AppException.notFound("Product not found"));
        if (body.containsKey("name")) p.setName((String) body.get("name"));
        if (body.containsKey("price")) p.setPrice(Double.parseDouble(body.get("price").toString()));
        if (body.containsKey("stock")) p.setStock(Integer.parseInt(body.get("stock").toString()));
        if (body.containsKey("description")) p.setDescription((String) body.get("description"));
        return ResponseEntity.ok(ApiResponse.ok(productRepo.save(p)));
    }
    @DeleteMapping("/vendor/products/{id}") public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        productRepo.deleteById(id); return ResponseEntity.ok(ApiResponse.ok(null, "Deleted"));
    }

    // ── Orders ──
    @GetMapping("/vendor/orders") public ResponseEntity<?> orders() {
        // In production, filter by vendor's products
        return ResponseEntity.ok(ApiResponse.ok(orderRepo.findAllByOrderByCreatedAtDesc()));
    }
    @GetMapping("/vendor/orders/{id}") public ResponseEntity<?> order(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(orderRepo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"))));
    }
    @PatchMapping("/vendor/orders/{id}") public ResponseEntity<?> updateOrder(@PathVariable String id, @RequestBody Map<String, String> body) {
        var order = orderRepo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"));
        order.setStatus(Order.OrderStatus.valueOf(body.get("status").toUpperCase()));
        return ResponseEntity.ok(ApiResponse.ok(orderRepo.save(order)));
    }

    // ── Analytics & Financials ──
    @GetMapping("/vendor/analytics") public ResponseEntity<?> analytics() {
        var v = vendor(); var products = productRepo.findByVendorId(v.getId());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("revenue", 0, "orders", v.getTotalOrders(), "products", products.size(), "avgRating", v.getRating(), "monthlySales", List.of(), "topProducts", List.of())));
    }
    @GetMapping("/vendor/financials") public ResponseEntity<?> financials() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("totalEarnings", 0, "pendingPayout", 0, "lastPayout", 0, "commissionRate", 10, "settlements", List.of())));
    }

    // ── Coupons ──
    @GetMapping("/vendor/coupons") public ResponseEntity<?> coupons() { return ResponseEntity.ok(ApiResponse.ok(couponRepo.findByVendorId(vendor().getId()))); }
    @PostMapping("/vendor/coupons") public ResponseEntity<?> createCoupon(@RequestBody Map<String, Object> body) {
        var c = Coupon.builder().code((String) body.get("code")).vendorId(vendor().getId())
            .discountType(Coupon.DiscountType.valueOf(body.getOrDefault("discountType", "PERCENT").toString().toUpperCase()))
            .discountValue(Double.parseDouble(body.getOrDefault("discountValue", 0).toString()))
            .minOrder(Double.parseDouble(body.getOrDefault("minOrder", 0).toString()))
            .label((String) body.getOrDefault("label", "")).description((String) body.getOrDefault("description", "")).build();
        return ResponseEntity.ok(ApiResponse.ok(couponRepo.save(c)));
    }
    @DeleteMapping("/vendor/coupons/{id}") public ResponseEntity<?> deleteCoupon(@PathVariable String id) {
        couponRepo.deleteById(id); return ResponseEntity.ok(ApiResponse.ok(null, "Deleted"));
    }

    // ── Inventory ──
    @GetMapping("/vendor/inventory") public ResponseEntity<?> inventory() { return ResponseEntity.ok(ApiResponse.ok(productRepo.findByVendorId(vendor().getId()))); }
    @PatchMapping("/vendor/inventory/{id}") public ResponseEntity<?> updateStock(@PathVariable String id, @RequestBody Map<String, Object> body) {
        var p = productRepo.findById(id).orElseThrow(() -> AppException.notFound("Product not found"));
        p.setStock(Integer.parseInt(body.get("stock").toString()));
        p.setStockStatus(p.getStock() == 0 ? Product.StockStatus.OUT_OF_STOCK : p.getStock() < 10 ? Product.StockStatus.LOW_STOCK : Product.StockStatus.IN_STOCK);
        return ResponseEntity.ok(ApiResponse.ok(productRepo.save(p)));
    }
    @GetMapping("/vendor/inventory/low-stock") public ResponseEntity<?> lowStock() {
        return ResponseEntity.ok(ApiResponse.ok(productRepo.findByVendorIdAndStockLessThan(vendor().getId(), 10)));
    }

    // ── Returns ──
    @GetMapping("/vendor/returns") public ResponseEntity<?> returns() {
        return ResponseEntity.ok(ApiResponse.ok(List.of())); // Filter RETURN_REQUESTED orders
    }
    @PatchMapping("/vendor/returns/{id}") public ResponseEntity<?> updateReturn(@PathVariable String id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("id", id, "status", body.get("status"))));
    }

    // ── Shipping ──
    @GetMapping("/vendor/shipping-settings") public ResponseEntity<?> shippingGet() {
        var s = shippingRepo.findByVendorId(vendor().getId());
        return ResponseEntity.ok(ApiResponse.ok(s.orElse(VendorShippingSettings.builder().vendorId(vendor().getId()).settings(Map.of()).build())));
    }
    @PutMapping("/vendor/shipping-settings") public ResponseEntity<?> shippingSet(@RequestBody Map<String, Object> body) {
        var v = vendor();
        var s = shippingRepo.findByVendorId(v.getId()).orElse(VendorShippingSettings.builder().vendorId(v.getId()).build());
        s.setSettings(body);
        return ResponseEntity.ok(ApiResponse.ok(shippingRepo.save(s)));
    }

    // ── Onboarding ──
    @PostMapping("/vendor/onboarding") public ResponseEntity<?> onboard(@RequestBody Map<String, Object> body) {
        audit.log("VENDOR_ONBOARDING", uid(), "Vendor onboarding submitted");
        return ResponseEntity.ok(ApiResponse.ok(Map.of("status", "submitted")));
    }

    // ── Store Customization ──
    @GetMapping("/vendor/store-customization") public ResponseEntity<?> customGet() {
        var c = customRepo.findByVendorId(vendor().getId());
        return ResponseEntity.ok(ApiResponse.ok(c.orElse(VendorStoreCustomization.builder().vendorId(vendor().getId()).customization(Map.of()).build())));
    }
    @PutMapping("/vendor/store-customization") public ResponseEntity<?> customSet(@RequestBody Map<String, Object> body) {
        var v = vendor();
        var c = customRepo.findByVendorId(v.getId()).orElse(VendorStoreCustomization.builder().vendorId(v.getId()).build());
        c.setCustomization(body);
        return ResponseEntity.ok(ApiResponse.ok(customRepo.save(c)));
    }

    // ── Payouts ──
    @GetMapping("/vendor/payouts") public ResponseEntity<?> payouts() {
        return ResponseEntity.ok(ApiResponse.ok(payoutRepo.findByVendorIdOrderByCreatedAtDesc(vendor().getId())));
    }

    // ── Bulk Upload ──
    @PostMapping("/vendor/products/bulk-upload") public ResponseEntity<?> bulkUpload(@RequestParam("file") MultipartFile file) {
        // CSV parsing would go here
        return ResponseEntity.ok(ApiResponse.ok(Map.of("total", 0, "success", 0, "errors", List.of())));
    }

    // ── Public Vendor Store ──
    @GetMapping("/vendors/{vendorId}") public ResponseEntity<?> publicStore(@PathVariable String vendorId) {
        return ResponseEntity.ok(ApiResponse.ok(vendorRepo.findById(vendorId).orElseThrow(() -> AppException.notFound("Vendor not found"))));
    }
    @GetMapping("/vendors/{vendorId}/products") public ResponseEntity<?> storeProducts(@PathVariable String vendorId) {
        return ResponseEntity.ok(ApiResponse.ok(productRepo.findByVendorId(vendorId)));
    }
}
