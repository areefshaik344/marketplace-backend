package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.ApiResponse;
import com.markethub.ecommerce.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/notifications") @RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository repo;
    private String uid() { return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(); }

    @GetMapping public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(repo.findByUserIdOrderByCreatedAtDesc(uid(), PageRequest.of(page, size))));
    }

    @GetMapping("/unread-count") public ResponseEntity<?> unread() { return ResponseEntity.ok(ApiResponse.ok(repo.countByUserIdAndReadFalse(uid()))); }

    @PatchMapping("/{id}/read") public ResponseEntity<?> markRead(@PathVariable String id) {
        var n = repo.findById(id).orElseThrow(); n.setRead(true); repo.save(n);
        return ResponseEntity.ok(ApiResponse.ok(null, "Marked read"));
    }

    @PatchMapping("/read-all") public ResponseEntity<?> markAllRead() {
        var items = repo.findByUserIdAndReadFalse(uid()); items.forEach(n -> n.setRead(true)); repo.saveAll(items);
        return ResponseEntity.ok(ApiResponse.ok(null, "All marked read"));
    }
}
