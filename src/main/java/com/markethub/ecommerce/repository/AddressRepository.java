package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AddressRepository extends JpaRepository<Address, String> {
    java.util.List<Address> findByUserId(String userId);
}
