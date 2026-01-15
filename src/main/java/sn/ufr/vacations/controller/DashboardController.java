package sn.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN_UFR')")
    public ResponseEntity<ApiResponse<?>> getAdminDashboard() {
        var dashboard = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/assistant")
    @PreAuthorize("hasRole('ASSISTANT_DEPARTEMENT')")
    public ResponseEntity<ApiResponse<?>> getAssistantDashboard() {
        var dashboard = dashboardService.getAssistantDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/vacataire")
    @PreAuthorize("hasRole('VACATAIRE')")
    public ResponseEntity<ApiResponse<?>> getVacataireDashboard() {
        var dashboard = dashboardService.getVacataireDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/financier")
    @PreAuthorize("hasRole('SERVICE_FINANCIER')")
    public ResponseEntity<ApiResponse<?>> getFinancierDashboard() {
        var dashboard = dashboardService.getFinancierDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}