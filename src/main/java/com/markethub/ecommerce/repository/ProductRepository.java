package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, String> {
    java.util.List<Product> findByVendorId(String vendorId);
    java.util.Optional<Product> findBySlug(String slug);
    java.util.List<Product> findByFeaturedTrue();
    java.util.List<Product> findByTrendingTrue();
    java.util.List<Product> findByDiscountGreaterThan(int d);
    java.util.List<Product> findByCategory(String category);
    org.springframework.data.domain.Page<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String cat, org.springframework.data.domain.Pageable p);
    java.util.List<Product> findByVendorIdAndStockLessThan(String vendorId, int threshold);
}
