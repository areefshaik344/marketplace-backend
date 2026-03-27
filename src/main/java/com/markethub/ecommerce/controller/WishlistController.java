package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.ApiResponse;
import com.markethub.ecommerce.entity.WishlistItem;
import com.markethub.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController @RequestMapping("/wishlist") @RequiredArgsConstructor
public class WishlistController {
    private final WishlistItemRepository wishRepo;
    private final ProductRepository productRepo;

    private String uid() { return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(); }

    @GetMapping public ResponseEntity<?> list() {
        var items = wishRepo.findByUserId(uid());
        var productIds = items.stream().map(WishlistItem::getProductId).collect(Collectors.toList());
        var products = productRepo.findAllById(productIds);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @PostMapping("/toggle") public ResponseEntity<?> toggle(@RequestBody Map<String, String> body) {
        String userId = uid(); String productId = body.get("productId");
        var existing = wishRepo.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) { wishRepo.delete(existing.get()); return ResponseEntity.ok(ApiResponse.ok(Map.of("added", false))); }
        wishRepo.save(WishlistItem.builder().userId(userId).productId(productId).build());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("added", true)));
    }
}
