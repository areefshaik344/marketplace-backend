package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.ApiResponse;
import com.markethub.ecommerce.entity.Order;
import com.markethub.ecommerce.exception.AppException;
import com.markethub.ecommerce.repository.OrderRepository;
import com.markethub.ecommerce.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController @RequestMapping("/orders") @RequiredArgsConstructor
public class OrderController {
    private final OrderRepository repo;
    private final AuditService audit;

    private String uid() { return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(); }

    @GetMapping public ResponseEntity<?> list() { return ResponseEntity.ok(ApiResponse.ok(repo.findByUserIdOrderByCreatedAtDesc(uid()))); }

    @GetMapping("/{id}") public ResponseEntity<?> byId(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(repo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"))));
    }

    @PostMapping public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        var order = Order.builder().userId(uid()).items((List<Map<String, Object>>) body.get("items"))
            .paymentMethod((String) body.get("paymentMethod")).shippingAddressId((String) body.get("shippingAddressId"))
            .total(Double.parseDouble(body.getOrDefault("total", 0).toString()))
            .grandTotal(Double.parseDouble(body.getOrDefault("total", 0).toString()))
            .timeline(List.of(Map.of("status", "PLACED", "timestamp", LocalDateTime.now().toString(), "description", "Order placed")))
            .build();
        return ResponseEntity.ok(ApiResponse.ok(repo.save(order)));
    }

    @PatchMapping("/{id}/cancel") public ResponseEntity<?> cancel(@PathVariable String id) {
        var order = repo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"));
        order.setStatus(Order.OrderStatus.CANCELLED); order.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.ok(repo.save(order)));
    }

    @PatchMapping("/{id}") public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        var order = repo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"));
        order.setStatus(Order.OrderStatus.valueOf(body.get("status").toUpperCase())); order.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.ok(repo.save(order)));
    }

    @PostMapping("/{id}/return") public ResponseEntity<?> requestReturn(@PathVariable String id, @RequestBody Map<String, String> body) {
        var order = repo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"));
        order.setStatus(Order.OrderStatus.RETURN_REQUESTED); order.setUpdatedAt(LocalDateTime.now());
        audit.log("RETURN_REQUESTED", uid(), "Return requested for order " + id);
        return ResponseEntity.ok(ApiResponse.ok(repo.save(order)));
    }

    @GetMapping("/{id}/track") public ResponseEntity<?> track(@PathVariable String id) {
        var order = repo.findById(id).orElseThrow(() -> AppException.notFound("Order not found"));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("status", order.getStatus(), "timeline", order.getTimeline() != null ? order.getTimeline() : List.of())));
    }
}
