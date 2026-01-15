package sn.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.request.CoursRequest;
import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.model.dto.response.CoursResponse;
import sn.ufr.vacations.service.CoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cours")
@RequiredArgsConstructor
public class CoursController {

    private final CoursService coursService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_UFR')")
    public ResponseEntity<ApiResponse<CoursResponse>> createCours(@Valid @RequestBody CoursRequest request) {
        CoursResponse response = coursService.createCours(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cours créé avec succès", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CoursResponse>>> getAllCours() {
        List<CoursResponse> response = coursService.getAllCours();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CoursResponse>> getCours(@PathVariable Long id) {
        CoursResponse response = coursService.getCours(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/filiere/{filiereId}")
    public ResponseEntity<ApiResponse<List<CoursResponse>>> getCoursByFiliere(@PathVariable Long filiereId) {
        List<CoursResponse> response = coursService.getCoursByFiliere(filiereId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/departement/{departementId}")
    public ResponseEntity<ApiResponse<List<CoursResponse>>> getCoursByDepartement(@PathVariable Long departementId) {
        List<CoursResponse> response = coursService.getCoursByDepartement(departementId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_UFR')")
    public ResponseEntity<ApiResponse<CoursResponse>> updateCours(
            @PathVariable Long id,
            @Valid @RequestBody CoursRequest request) {
        CoursResponse response = coursService.updateCours(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cours modifié avec succès", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> deleteCours(@PathVariable Long id) {
        coursService.deleteCours(id);
        return ResponseEntity.ok(ApiResponse.success("Cours supprimé avec succès", null));
    }
}