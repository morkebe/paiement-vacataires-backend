package sn.ufr.vacations.service;

import sn.ufr.vacations.model.entity.AuditLog;
import sn.ufr.vacations.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    public List<AuditLog> getAuditLogsByUser(String username) {
        return auditLogRepository.findAll().stream()
                .filter(log -> log.getUtilisateur().equals(username))
                .collect(Collectors.toList());
    }

    public List<AuditLog> getAuditLogsByEntite(String entite, Long entiteId) {
        return auditLogRepository.findAll().stream()
                .filter(log -> log.getEntite().equals(entite) && log.getEntiteId().equals(entiteId))
                .collect(Collectors.toList());
    }

    public List<AuditLog> getAuditLogsByPeriode(LocalDateTime debut, LocalDateTime fin) {
        return auditLogRepository.findAll().stream()
                .filter(log -> !log.getTimestamp().isBefore(debut) && !log.getTimestamp().isAfter(fin))
                .collect(Collectors.toList());
    }

    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findAll().stream()
                .filter(log -> log.getAction().equals(action))
                .collect(Collectors.toList());
    }
}