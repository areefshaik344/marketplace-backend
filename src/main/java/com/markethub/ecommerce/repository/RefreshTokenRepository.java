package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    java.util.Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByUserId(String userId);
    void deleteByTokenHash(String tokenHash);
}
