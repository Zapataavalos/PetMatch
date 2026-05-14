package com.petmatch.msreport.controller;

import com.petmatch.msreport.dto.ReportDTO;
import com.petmatch.msreport.dto.ReportResponseDTO;
import com.petmatch.msreport.dto.ReportUpdateStatusDTO;
import com.petmatch.msreport.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ── GET ────────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<ReportResponseDTO>> getAll() {
        return ResponseEntity.ok(reportService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<ReportResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getById(id));
    }

    @GetMapping("/user/{idUser}")
    @PreAuthorize("hasAnyRole('ADMIN','DUENO')")
    public ResponseEntity<List<ReportResponseDTO>> getByUser(@PathVariable Long idUser) {
        return ResponseEntity.ok(reportService.getByUser(idUser));
    }

    @GetMapping("/pet/{idPet}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<ReportResponseDTO>> getByPet(@PathVariable Long idPet) {
        return ResponseEntity.ok(reportService.getByPet(idPet));
    }

    @GetMapping("/type/{idType}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<ReportResponseDTO>> getByType(@PathVariable Long idType) {
        return ResponseEntity.ok(reportService.getByType(idType));
    }

    @GetMapping("/status/{idStatus}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<ReportResponseDTO>> getByStatus(@PathVariable Long idStatus) {
        return ResponseEntity.ok(reportService.getByStatus(idStatus));
    }

    // ── POST ───────────────────────────────────────────────────────────────

    /**
     * El cliente envía idType (1, 2 o 3).
     * El Factory Method asigna el estado automáticamente.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<ReportResponseDTO> create(@Valid @RequestBody ReportDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.save(dto));
    }

    // ── PATCH ──────────────────────────────────────────────────────────────

    /** Actualiza únicamente el estado de un reporte existente. */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','DUENO')")
    public ResponseEntity<ReportResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReportUpdateStatusDTO dto) {
        return ResponseEntity.ok(reportService.updateStatus(id, dto.getIdStatus()));
    }

    // ── DELETE ─────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
