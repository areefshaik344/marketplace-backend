package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    java.util.List<CartItem> findByUserId(String userId);
    java.util.Optional<CartItem> findByUserIdAndProductId(String userId, String productId);
    void deleteByUserId(String userId);
}
