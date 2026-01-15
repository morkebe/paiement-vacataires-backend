package sn.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.request.FiliereRequest;
import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.model.dto.response.FiliereResponse;
import sn.ufr.vacations.service.FiliereService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filieres")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_UFR')")
public class FiliereController {

    private final FiliereService filiereService;

    @PostMapping
    public ResponseEntity<ApiResponse<FiliereResponse>> createFiliere(
            @Valid @RequestBody FiliereRequest request) {
        FiliereResponse response = filiereService.createFiliere(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Filière créée avec succès", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_UFR', 'ASSISTANT_DEPARTEMENT', 'SERVICE_FINANCIER')")
    public ResponseEntity<ApiResponse<List<FiliereResponse>>> getAllFilieres() {
        List<FiliereResponse> response = filiereService.getAllFilieres();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FiliereResponse>> getFiliere(@PathVariable Long id) {
        FiliereResponse response = filiereService.getFiliere(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/departement/{departementId}")
    @PreAuthorize("hasAnyRole('ADMIN_UFR', 'ASSISTANT_DEPARTEMENT')")
    public ResponseEntity<ApiResponse<List<FiliereResponse>>> getFilieresByDepartement(
            @PathVariable Long departementId) {
        List<FiliereResponse> response = filiereService.getFilieresByDepartement(departementId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FiliereResponse>> updateFiliere(
            @PathVariable Long id,
            @Valid @RequestBody FiliereRequest request) {
        FiliereResponse response = filiereService.updateFiliere(id, request);
        return ResponseEntity.ok(ApiResponse.success("Filière modifiée avec succès", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFiliere(@PathVariable Long id) {
        filiereService.deleteFiliere(id);
        return ResponseEntity.ok(ApiResponse.success("Filière supprimée avec succès", null));
    }
}
