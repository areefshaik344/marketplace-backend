package com.markethub.ecommerce.repository;
import com.markethub.ecommerce.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, String> {}
