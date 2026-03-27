package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NotificationRepository extends JpaRepository<Notification, String> {
    org.springframework.data.domain.Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, org.springframework.data.domain.Pageable p);
    long countByUserIdAndReadFalse(String userId);
    java.util.List<Notification> findByUserIdAndReadFalse(String userId);
}
