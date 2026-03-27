package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CouponRepository extends JpaRepository<Coupon, String> {
    java.util.Optional<Coupon> findByCode(String code);
    java.util.List<Coupon> findByVendorId(String vendorId);
}
