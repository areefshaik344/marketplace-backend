package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.ApiResponse;
import com.markethub.ecommerce.entity.CartItem;
import com.markethub.ecommerce.exception.AppException;
import com.markethub.ecommerce.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/cart") @RequiredArgsConstructor
public class CartController {
    private final CartItemRepository repo;

    private String uid() { return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(); }

    @GetMapping public ResponseEntity<?> get() { return ResponseEntity.ok(ApiResponse.ok(repo.findByUserId(uid()))); }

    @PostMapping("/items") public ResponseEntity<?> add(@RequestBody Map<String, Object> body) {
        String userId = uid();
        String productId = (String) body.get("productId");
        int qty = body.get("quantity") instanceof Integer ? (Integer) body.get("quantity") : Integer.parseInt(body.get("quantity").toString());
        var existing = repo.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) { var item = existing.get(); item.setQuantity(item.getQuantity() + qty); return ResponseEntity.ok(ApiResponse.ok(repo.save(item))); }
        var item = CartItem.builder().userId(userId).productId(productId).quantity(qty)
            .variant(body.get("variant") != null ? body.get("variant").toString() : null).build();
        return ResponseEntity.ok(ApiResponse.ok(repo.save(item)));
    }

    @PutMapping("/items/{id}") public ResponseEntity<?> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        var item = repo.findById(id).orElseThrow(() -> AppException.notFound("Cart item not found"));
        item.setQuantity(Integer.parseInt(body.get("quantity").toString()));
        return ResponseEntity.ok(ApiResponse.ok(repo.save(item)));
    }

    @DeleteMapping("/items/{id}") public ResponseEntity<?> remove(@PathVariable String id) {
        repo.deleteById(id); return ResponseEntity.ok(ApiResponse.ok(null, "Removed"));
    }

    @DeleteMapping("/clear") public ResponseEntity<?> clear() {
        repo.deleteByUserId(uid()); return ResponseEntity.ok(ApiResponse.ok(null, "Cart cleared"));
    }

    @PostMapping("/validate-coupon") public ResponseEntity<?> validateCoupon(@RequestBody Map<String, Object> body) {
        // Coupon validation logic would go here
        return ResponseEntity.ok(ApiResponse.ok(Map.of("coupon", body, "discountAmount", 0)));
    }

    @PostMapping("/shipping") public ResponseEntity<?> shipping(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("cost", 49, "estimatedDays", 5, "freeAbove", 499)));
    }
}
