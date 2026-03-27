package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReviewRepository extends JpaRepository<Review, String> {
    java.util.List<Review> findByProductId(String productId);
}
