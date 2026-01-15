package sn.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.request.PaiementRequest;
import sn.ufr.vacations.model.dto.request.ValidationPaiementRequest;
import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.model.dto.response.PaiementResponse;
import sn.ufr.vacations.model.enums.StatutPaiement;
import sn.ufr.vacations.service.AuthService;
import sn.ufr.vacations.service.PaiementService;
import sn.ufr.vacations.service.VacataireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;
    private final AuthService authService;
    private final VacataireService vacataireService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<PaiementResponse>> createPaiement(
            @Valid @RequestBody PaiementRequest request) {
        String createdBy = authService.getCurrentUser().getUsername();
        PaiementResponse response = paiementService.createPaiement(
                request.getVacataireId(),
                request.getAttributionIds(),
                createdBy
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Paiement créé avec succès", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR', 'SERVICE_FINANCIER')")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getAllPaiements() {
        List<PaiementResponse> response = paiementService.getAllPaiements();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaiementResponse>> getPaiement(@PathVariable Long id) {
        PaiementResponse response = paiementService.getPaiement(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('VACATAIRE')")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getMyPaiements() {
        Long userId = authService.getCurrentUser().getId();
        var vacataire = vacataireService.getVacataireByUserId(userId);
        List<PaiementResponse> response = paiementService.getPaiementsByVacataire(vacataire.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vacataire/{vacataireId}")
    @PreAuthorize("hasAnyRole('ASSISTANT_DEPARTEMENT', 'ADMIN_UFR', 'SERVICE_FINANCIER')")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByVacataire(
            @PathVariable Long vacataireId) {
        List<PaiementResponse> response = paiementService.getPaiementsByVacataire(vacataireId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasAnyRole('SERVICE_FINANCIER', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByStatut(
            @PathVariable StatutPaiement statut) {
        List<PaiementResponse> response = paiementService.getPaiementsByStatut(statut);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasAnyRole('SERVICE_FINANCIER', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsEnAttente() {
        List<PaiementResponse> response = paiementService.getPaiementsByStatut(StatutPaiement.EN_ATTENTE);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('SERVICE_FINANCIER', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> validatePaiement(@PathVariable Long id) {
        String validateur = authService.getCurrentUser().getUsername();
        paiementService.validatePaiement(id, validateur);
        return ResponseEntity.ok(ApiResponse.success("Paiement validé avec succès", null));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('SERVICE_FINANCIER', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> rejectPaiement(
            @PathVariable Long id,
            @Valid @RequestBody ValidationPaiementRequest request) {
        String rejetePar = authService.getCurrentUser().getUsername();
        paiementService.rejectPaiement(id, request.getMotifRejet(), rejetePar);
        return ResponseEntity.ok(ApiResponse.success("Paiement rejeté", null));
    }

    @PostMapping("/{id}/marquer-paye")
    @PreAuthorize("hasAnyRole('SERVICE_FINANCIER', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> marquerCommePaye(@PathVariable Long id) {
        paiementService.marquerCommePaye(id);
        return ResponseEntity.ok(ApiResponse.success("Paiement marqué comme payé", null));
    }

    @GetMapping("/{id}/bordereau")
    public ResponseEntity<Resource> downloadBordereau(@PathVariable Long id) {
        Resource resource = paiementService.generateBordereau(id);
        String filename = "bordereau_" + id + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/statistiques")
    @PreAuthorize("hasAnyRole('SERVICE_FINANCIER', 'ADMIN_UFR')")
    public ResponseEntity<ApiResponse<?>> getStatistiques() {
        var stats = paiementService.getStatistiques();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}