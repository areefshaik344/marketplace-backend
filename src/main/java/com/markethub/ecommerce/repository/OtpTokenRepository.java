package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, String> {
    Optional<OtpToken> findByPhone(String phone);
    void deleteByPhone(String phone);
}
