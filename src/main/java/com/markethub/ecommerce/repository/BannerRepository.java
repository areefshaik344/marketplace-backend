package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BannerRepository extends JpaRepository<Banner, String> {
    java.util.List<Banner> findByActiveTrueOrderBySortOrder();
}
