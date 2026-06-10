package com.petmatch.msreporttype.controller;

import com.petmatch.msreporttype.dto.ReportTypeRequest;
import com.petmatch.msreporttype.dto.ReportTypeResponse;
import com.petmatch.msreporttype.service.ReportTypeService;
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
@RequestMapping("/api/report-type")
public class ReportTypeController {

    private final ReportTypeService reportTypeService;

    public ReportTypeController(ReportTypeService reportTypeService) {
        this.reportTypeService = reportTypeService;
    }

    @GetMapping
    public ResponseEntity<List<ReportTypeResponse>> listar() {
        return ResponseEntity.ok(reportTypeService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportTypeResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(reportTypeService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ReportTypeResponse> crear(@Valid @RequestBody ReportTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportTypeService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportTypeResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReportTypeRequest request
    ) {
        return ResponseEntity.ok(reportTypeService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reportTypeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
