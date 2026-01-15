package sn.ufr.vacations.service;

import sn.ufr.vacations.model.entity.AuditLog;
import sn.ufr.vacations.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String action, String entite, Long entiteId, String details, String utilisateur) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entite(entite)
                .entiteId(entiteId)
                .details(details)
                .utilisateur(utilisateur)
                .build();

        auditLogRepository.save(auditLog);
    }
}