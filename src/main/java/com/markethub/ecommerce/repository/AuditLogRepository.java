package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
    java.util.List<AuditLog> findAllByOrderByCreatedAtDesc();
}
