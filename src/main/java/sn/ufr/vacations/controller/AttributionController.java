package sn.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.request.AttributionRequest;
import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.model.dto.response.AttributionResponse;
import sn.ufr.vacations.service.AttributionService;
import sn.ufr.vacations.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attributions")
@RequiredArgsConstructor
public class AttributionController {

    private final AttributionService attributionService;
    private final AuthService authService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<AttributionResponse>> createAttribution(
            @Valid @RequestBody AttributionRequest request) {
        String createdBy = authService.getCurrentUser().getUsername();
        AttributionResponse response = attributionService.createAttribution(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Attribution créée avec succès", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR', 'SERVICE_FINANCIER')")
    public ResponseEntity<ApiResponse<List<AttributionResponse>>> getAllAttributions() {
        List<AttributionResponse> response = attributionService.getAllAttributions();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttributionResponse>> getAttribution(@PathVariable Long id) {
        AttributionResponse response = attributionService.getAttribution(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vacataire/{vacataireId}")
    public ResponseEntity<ApiResponse<List<AttributionResponse>>> getAttributionsByVacataire(
            @PathVariable Long vacataireId) {
        List<AttributionResponse> response = attributionService.getAttributionsByVacataire(vacataireId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/cours/{coursId}")
    public ResponseEntity<ApiResponse<List<AttributionResponse>>> getAttributionsByCours(
            @PathVariable Long coursId) {
        List<AttributionResponse> response = attributionService.getAttributionsByCours(coursId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<AttributionResponse>> updateAttribution(
            @PathVariable Long id,
            @Valid @RequestBody AttributionRequest request) {
        AttributionResponse response = attributionService.updateAttribution(id, request);
        return ResponseEntity.ok(ApiResponse.success("Attribution modifiée avec succès", response));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> submitAttribution(@PathVariable Long id) {
        String submittedBy = authService.getCurrentUser().getUsername();
        attributionService.submitAttribution(id, submittedBy);
        return ResponseEntity.ok(ApiResponse.success("Attribution soumise avec succès", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> deleteAttribution(@PathVariable Long id) {
        attributionService.deleteAttribution(id);
        return ResponseEntity.ok(ApiResponse.success("Attribution supprimée avec succès", null));
    }
}