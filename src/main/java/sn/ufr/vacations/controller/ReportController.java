package com.ufr.vacations.controller;

import sn.ufr.vacations.model.dto.response.ApiResponse;
import sn.ufr.vacations.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN_UFR', 'SERVICE_FINANCIER')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/paiements/periode")
    public ResponseEntity<ApiResponse<?>> getPaiementsParPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        var report = reportService.getPaiementsParPeriode(debut, fin);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/paiements/departement/{departementId}")
    public ResponseEntity<ApiResponse<?>> getPaiementsParDepartement(@PathVariable Long departementId) {
        var report = reportService.getPaiementsParDepartement(departementId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/vacataires/activite")
    public ResponseEntity<ApiResponse<?>> getActiviteVacataires() {
        var report = reportService.getActiviteVacataires();
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/cours/attribution")
    public ResponseEntity<ApiResponse<?>> getStatistiquesAttribution() {
        var report = reportService.getStatistiquesAttribution();
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/export/paiements")
    public ResponseEntity<Resource> exportPaiements(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "pdf") String format) {

        Resource resource = reportService.exportPaiements(debut, fin, format);
        String filename = "rapport_paiements_" + debut + "_" + fin + "." + format;

        MediaType mediaType = format.equals("pdf") ? MediaType.APPLICATION_PDF :
                MediaType.parseMediaType("application/vnd.ms-excel");

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}