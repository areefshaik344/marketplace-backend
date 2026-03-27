package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, String> {
    java.util.List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
    java.util.List<Order> findAllByOrderByCreatedAtDesc();
}
