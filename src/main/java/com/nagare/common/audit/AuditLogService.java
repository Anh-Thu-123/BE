package com.nagare.common.audit;

import com.nagare.identity.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String entity, String entityId, String summary) {
        AuditLog log = new AuditLog();
        var user = SecurityUtils.currentUserOrNull();
        if (user != null) {
            log.setActorId(user.getId());
            log.setActorUsername(user.getUsername());
        }
        log.setAction(action);
        log.setEntity(entity);
        log.setEntityId(entityId);
        log.setSummary(summary);
        auditLogRepository.save(log);
    }
}
