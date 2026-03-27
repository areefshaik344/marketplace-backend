package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.CommissionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CommissionConfigRepository extends JpaRepository<CommissionConfig, String> {
    java.util.List<CommissionConfig> findByIsOverrideTrue();
    java.util.Optional<CommissionConfig> findByCategory(String category);
}
