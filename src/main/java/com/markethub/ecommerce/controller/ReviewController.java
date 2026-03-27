package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.ApiResponse;
import com.markethub.ecommerce.entity.Review;
import com.markethub.ecommerce.exception.AppException;
import com.markethub.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequiredArgsConstructor
public class ReviewController {
    private final ReviewRepository repo;
    private final UserRepository userRepo;

//    @GetMapping("/products/{productId}/reviews")
//    public ResponseEntity<?> byProduct(@PathVariable String productId) {
//        return ResponseEntity.ok(ApiResponse.ok(repo.findByProductId(productId)));
//    }

    @PostMapping("/reviews")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var user = userRepo.findById(userId).orElseThrow();
        var review = Review.builder().productId((String) body.get("productId")).userId(userId).userName(user.getName())
            .rating(Integer.parseInt(body.get("rating").toString())).title((String) body.get("title"))
            .comment((String) body.get("comment")).verified(true).build();
        return ResponseEntity.ok(ApiResponse.ok(repo.save(review)));
    }

    @PatchMapping("/reviews/{id}/helpful")
    public ResponseEntity<?> helpful(@PathVariable String id) {
        var review = repo.findById(id).orElseThrow(() -> AppException.notFound("Review not found"));
        review.setHelpful(review.getHelpful() + 1);
        return ResponseEntity.ok(ApiResponse.ok(repo.save(review)));
    }
}
