package sn.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.request.DepartementRequest;
import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.model.dto.response.DepartementResponse;
import sn.ufr.vacations.service.DepartementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_UFR')")
public class DepartementController {

    private final DepartementService departementService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_UFR')")
    public ResponseEntity<ApiResponse<DepartementResponse>> createDepartement(
            @Valid @RequestBody DepartementRequest request) {
        DepartementResponse response = departementService.createDepartement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Département créé avec succès", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartementResponse>>> getAllDepartements() {
        List<DepartementResponse> response = departementService.getAllDepartements();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_UFR')")
    public ResponseEntity<ApiResponse<DepartementResponse>> getDepartement(@PathVariable Long id) {
        DepartementResponse response = departementService.getDepartement(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_UFR')")
    public ResponseEntity<ApiResponse<DepartementResponse>> updateDepartement(
            @PathVariable Long id,
            @Valid @RequestBody DepartementRequest request) {
        DepartementResponse response = departementService.updateDepartement(id, request);
        return ResponseEntity.ok(ApiResponse.success("Département modifié avec succès", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_UFR')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartement(@PathVariable Long id) {
        departementService.deleteDepartement(id);
        return ResponseEntity.ok(ApiResponse.success("Département supprimé avec succès", null));
    }
}