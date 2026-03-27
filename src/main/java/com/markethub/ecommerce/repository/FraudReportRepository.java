package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.FraudReport;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FraudReportRepository extends JpaRepository<FraudReport, String> {
    java.util.List<FraudReport> findByType(String type);
}
