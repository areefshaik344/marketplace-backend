package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.VendorStoreCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VendorStoreCustomizationRepository extends JpaRepository<VendorStoreCustomization, String> {
    java.util.Optional<VendorStoreCustomization> findByVendorId(String vendorId);
}
