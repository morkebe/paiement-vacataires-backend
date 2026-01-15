package sn.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.request.VacataireRequest;
import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.model.dto.response.VacataireResponse;
import sn.ufr.vacations.service.AuthService;
import sn.ufr.vacations.service.VacataireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacataires")
@RequiredArgsConstructor
public class VacataireController {

    private final VacataireService vacataireService;
    private final AuthService authService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<VacataireResponse>> createVacataire(
            @Valid @RequestBody VacataireRequest request) {
        String createdBy = authService.getCurrentUser().getUsername();
        VacataireResponse response = vacataireService.createVacataire(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vacataire créé avec succès", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR', 'SERVICE_FINANCIER')")
    public ResponseEntity<ApiResponse<List<VacataireResponse>>> getAllVacataires() {
        List<VacataireResponse> response = vacataireService.getAllVacataires();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VacataireResponse>> getVacataire(@PathVariable Long id) {
        VacataireResponse response = vacataireService.getVacataire(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('VACATAIRE')")
    public ResponseEntity<ApiResponse<VacataireResponse>> getCurrentVacataire() {
        Long userId = authService.getCurrentUser().getId();
        VacataireResponse response = vacataireService.getVacataireByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/departement/{departementId}")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<List<VacataireResponse>>> getVacatairesByDepartement(
            @PathVariable Long departementId) {
        List<VacataireResponse> response = vacataireService.getVacatairesByDepartement(departementId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<VacataireResponse>> updateVacataire(
            @PathVariable Long id,
            @Valid @RequestBody VacataireRequest request) {
        VacataireResponse response = vacataireService.updateVacataire(id, request);
        return ResponseEntity.ok(ApiResponse.success("Vacataire modifié avec succès", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> deleteVacataire(@PathVariable Long id) {
        vacataireService.deleteVacataire(id);
        return ResponseEntity.ok(ApiResponse.success("Vacataire supprimé avec succès", null));
    }
}
