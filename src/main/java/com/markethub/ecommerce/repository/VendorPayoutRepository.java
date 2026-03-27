package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.VendorPayout;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VendorPayoutRepository extends JpaRepository<VendorPayout, String> {
    java.util.List<VendorPayout> findByVendorIdOrderByCreatedAtDesc(String vendorId);
}
