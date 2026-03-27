package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VendorRepository extends JpaRepository<Vendor, String> {
    java.util.Optional<Vendor> findByUserId(String userId);
    java.util.List<Vendor> findByStatus(Vendor.VendorStatus status);
}
