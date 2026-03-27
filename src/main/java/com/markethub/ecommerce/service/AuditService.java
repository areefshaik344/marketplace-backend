package com.markethub.ecommerce.service;
import com.markethub.ecommerce.entity.AuditLog;
import com.markethub.ecommerce.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository repo;
    public void log(String action, String userId, String userName, String details, AuditLog.Severity severity) {
        repo.save(AuditLog.builder().action(action).userId(userId).userName(userName).details(details).severity(severity).build());
    }
    public void log(String action, String userId, String details) {
        log(action, userId, "", details, AuditLog.Severity.INFO);
    }
}
