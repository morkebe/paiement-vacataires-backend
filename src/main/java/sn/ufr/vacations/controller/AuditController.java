package sn.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_UFR')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllAuditLogs() {
        var logs = auditService.getAllAuditLogs();
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/utilisateur/{username}")
    public ResponseEntity<ApiResponse<?>> getAuditLogsByUser(@PathVariable String username) {
        var logs = auditService.getAuditLogsByUser(username);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/entite/{entite}/{entiteId}")
    public ResponseEntity<ApiResponse<?>> getAuditLogsByEntite(
            @PathVariable String entite,
            @PathVariable Long entiteId) {
        var logs = auditService.getAuditLogsByEntite(entite, entiteId);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/periode")
    public ResponseEntity<ApiResponse<?>> getAuditLogsByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        var logs = auditService.getAuditLogsByPeriode(debut, fin);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<ApiResponse<?>> getAuditLogsByAction(@PathVariable String action) {
        var logs = auditService.getAuditLogsByAction(action);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
