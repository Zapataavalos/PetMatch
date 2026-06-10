package com.petmatch.msreportstatus.controller;

import com.petmatch.msreportstatus.dto.ReportStatusRequest;
import com.petmatch.msreportstatus.dto.ReportStatusResponse;
import com.petmatch.msreportstatus.service.ReportStatusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/report-status")
public class ReportStatusController {

    private final ReportStatusService reportStatusService;

    public ReportStatusController(ReportStatusService reportStatusService) {
        this.reportStatusService = reportStatusService;
    }

    @GetMapping
    public ResponseEntity<List<ReportStatusResponse>> listar() {
        return ResponseEntity.ok(reportStatusService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportStatusResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(reportStatusService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ReportStatusResponse> crear(@Valid @RequestBody ReportStatusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportStatusService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportStatusResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReportStatusRequest request
    ) {
        return ResponseEntity.ok(reportStatusService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reportStatusService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
