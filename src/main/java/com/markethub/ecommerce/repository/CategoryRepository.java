package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category, String> {
    java.util.Optional<Category> findBySlug(String slug);
    java.util.List<Category> findByActiveTrue();
}
