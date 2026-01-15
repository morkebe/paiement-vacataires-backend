package sn.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.request.PointageRequest;
import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.model.dto.response.PointageResponse;
import sn.ufr.vacations.service.AuthService;
import sn.ufr.vacations.service.PointageService;
import sn.ufr.vacations.service.VacataireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pointages")
@RequiredArgsConstructor
public class PointageController {

    private final PointageService pointageService;
    private final AuthService authService;
    private final VacataireService vacataireService;

    @PostMapping
    @PreAuthorize("hasRole('VACATAIRE')")
    public ResponseEntity<ApiResponse<PointageResponse>> createPointage(
            @Valid @RequestBody PointageRequest request) {
        Long userId = authService.getCurrentUser().getId();
        var vacataire = vacataireService.getVacataireByUserId(userId);
        PointageResponse response = pointageService.createPointage(request, vacataire.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pointage enregistré avec succès", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR', 'SERVICE_FINANCIER')")
    public ResponseEntity<ApiResponse<List<PointageResponse>>> getAllPointages() {
        List<PointageResponse> response = pointageService.getAllPointages();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PointageResponse>> getPointage(@PathVariable Long id) {
        PointageResponse response = pointageService.getPointage(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('VACATAIRE')")
    public ResponseEntity<ApiResponse<List<PointageResponse>>> getMyPointages() {
        Long userId = authService.getCurrentUser().getId();
        var vacataire = vacataireService.getVacataireByUserId(userId);
        List<PointageResponse> response = pointageService.getPointagesByVacataire(vacataire.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vacataire/{vacataireId}")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR', 'SERVICE_FINANCIER')")
    public ResponseEntity<ApiResponse<List<PointageResponse>>> getPointagesByVacataire(
            @PathVariable Long vacataireId) {
        List<PointageResponse> response = pointageService.getPointagesByVacataire(vacataireId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/attribution/{attributionId}")
    public ResponseEntity<ApiResponse<List<PointageResponse>>> getPointagesByAttribution(
            @PathVariable Long attributionId) {
        List<PointageResponse> response = pointageService.getPointagesByAttribution(attributionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vacataire/{vacataireId}/periode")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR', 'SERVICE_FINANCIER')")
    public ResponseEntity<ApiResponse<List<PointageResponse>>> getPointagesByPeriode(
            @PathVariable Long vacataireId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<PointageResponse> response = pointageService.getPointagesByPeriode(vacataireId, debut, fin);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VACATAIRE')")
    public ResponseEntity<ApiResponse<PointageResponse>> updatePointage(
            @PathVariable Long id,
            @Valid @RequestBody PointageRequest request) {
        PointageResponse response = pointageService.updatePointage(id, request);
        return ResponseEntity.ok(ApiResponse.success("Pointage modifié avec succès", response));
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> validatePointage(@PathVariable Long id) {
        String validateur = authService.getCurrentUser().getUsername();
        pointageService.validatePointage(id, validateur);
        return ResponseEntity.ok(ApiResponse.success("Pointage validé avec succès", null));
    }

    @PostMapping("/validate-batch")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> validatePointagesBatch(@RequestBody List<Long> pointageIds) {
        String validateur = authService.getCurrentUser().getUsername();
        pointageService.validatePointagesBatch(pointageIds, validateur);
        return ResponseEntity.ok(ApiResponse.success("Pointages validés avec succès", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VACATAIRE', 'ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> deletePointage(@PathVariable Long id) {
        pointageService.deletePointage(id);
        return ResponseEntity.ok(ApiResponse.success("Pointage supprimé avec succès", null));
    }
}