package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.*;
import com.markethub.ecommerce.entity.Product;
import com.markethub.ecommerce.exception.AppException;
import com.markethub.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController @RequestMapping("/products") @RequiredArgsConstructor
public class ProductController {
    private final ProductRepository repo;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category, @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minRating, @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy) {
        Pageable pg = PageRequest.of(page, size, resolveSort(sortBy));
        Page<Product> result;
        if (search != null && !search.isBlank()) {
            result = repo.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(search, search, pg);
        } else {
            result = repo.findAll(pg);
        }
        var data = PaginatedData.<Product>builder().items(result.getContent()).total(result.getTotalElements())
            .page(page).pageSize(size).totalPages(result.getTotalPages()).build();
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/{id}") public ResponseEntity<?> byId(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(repo.findById(id).orElseThrow(() -> AppException.notFound("Product not found"))));
    }

    @GetMapping("/slug/{slug}") public ResponseEntity<?> bySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(repo.findBySlug(slug).orElseThrow(() -> AppException.notFound("Product not found"))));
    }

    @GetMapping("/categories") public ResponseEntity<?> categories() {
        var cats = repo.findAll().stream().map(Product::getCategory).distinct().sorted().collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(cats));
    }

    @GetMapping("/brands") public ResponseEntity<?> brands() {
        var brands = repo.findAll().stream().map(Product::getBrand).distinct().sorted().collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(brands));
    }

    @GetMapping("/search") public ResponseEntity<?> search(@RequestParam String q, @RequestParam(defaultValue = "6") int limit) {
        var results = repo.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(q, q, PageRequest.of(0, limit));
        return ResponseEntity.ok(ApiResponse.ok(results.getContent()));
    }

    @GetMapping("/featured") public ResponseEntity<?> featured() { return ResponseEntity.ok(ApiResponse.ok(repo.findByFeaturedTrue())); }
    @GetMapping("/trending") public ResponseEntity<?> trending() { return ResponseEntity.ok(ApiResponse.ok(repo.findByTrendingTrue())); }
    @GetMapping("/deals") public ResponseEntity<?> deals() { return ResponseEntity.ok(ApiResponse.ok(repo.findByDiscountGreaterThan(0))); }

    @GetMapping("/{id}/related") public ResponseEntity<?> related(@PathVariable String id, @RequestParam(defaultValue = "4") int limit) {
        var product = repo.findById(id).orElseThrow(() -> AppException.notFound("Product not found"));
        var related = repo.findByCategory(product.getCategory()).stream().filter(p -> !p.getId().equals(id)).limit(limit).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(related));
    }

    @GetMapping("/{productId}/reviews") public ResponseEntity<?> reviews(@PathVariable String productId) {
        // Delegated to ReviewController for actual data
        return ResponseEntity.ok(ApiResponse.ok(List.of()));
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null) return Sort.by("createdAt").descending();
        return switch (sortBy) {
            case "price-asc" -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "rating" -> Sort.by("rating").descending();
            case "discount" -> Sort.by("discount").descending();
            case "newest" -> Sort.by("createdAt").descending();
            default -> Sort.by("createdAt").descending();
        };
    }
}
