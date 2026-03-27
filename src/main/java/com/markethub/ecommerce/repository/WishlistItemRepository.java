package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WishlistItemRepository extends JpaRepository<WishlistItem, String> {
    java.util.List<WishlistItem> findByUserId(String userId);
    java.util.Optional<WishlistItem> findByUserIdAndProductId(String userId, String productId);
}
