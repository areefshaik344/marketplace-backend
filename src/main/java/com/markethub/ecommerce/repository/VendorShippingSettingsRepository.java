package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.VendorShippingSettings;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VendorShippingSettingsRepository extends JpaRepository<VendorShippingSettings, String> {
    java.util.Optional<VendorShippingSettings> findByVendorId(String vendorId);
}
