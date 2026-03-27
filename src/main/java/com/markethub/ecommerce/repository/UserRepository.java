package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, String> {
    java.util.Optional<User> findByEmail(String email);
    java.util.Optional<User> findByResetPasswordToken(String token);
    java.util.Optional<User> findByEmailVerificationToken(String token);
    java.util.List<User> findByRole(User.Role role);
    java.util.Optional<User> findByPhone(String phone);
}
