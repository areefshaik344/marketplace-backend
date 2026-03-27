package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.VendorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VendorApplicationRepository extends JpaRepository<VendorApplication, String> {
    java.util.List<VendorApplication> findByStatus(VendorApplication.AppStatus status);
}
