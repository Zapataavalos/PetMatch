package com.petmatch.msreport.controller;

import com.petmatch.msreport.dto.ReportDTO;
import com.petmatch.msreport.dto.ReportResponseDTO;
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

    // Factory Method crea el reporte automáticamente según idType
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<ReportResponseDTO> create(@Valid @RequestBody ReportDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.save(dto));
    }

    // Actualizar solo el estado del reporte
    @PatchMapping("/{id}/status/{idStatus}")
    @PreAuthorize("hasAnyRole('ADMIN','DUENO')")
    public ResponseEntity<ReportResponseDTO> updateStatus(@PathVariable Long id,
                                                           @PathVariable Long idStatus) {
        return ResponseEntity.ok(reportService.updateStatus(id, idStatus));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
