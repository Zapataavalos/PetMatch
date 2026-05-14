package com.petmatch.msreportstatus.controller;
import com.petmatch.msreportstatus.dto.ReportStatusDTO;
import com.petmatch.msreportstatus.dto.ReportStatusResponseDTO;
import com.petmatch.msreportstatus.service.ReportStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/report-status") @RequiredArgsConstructor
public class ReportStatusController {
    private final ReportStatusService service;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<ReportStatusResponseDTO>> getAll(){ return ResponseEntity.ok(service.getAll()); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<ReportStatusResponseDTO> getById(@PathVariable Long id){ return ResponseEntity.ok(service.getById(id)); }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportStatusResponseDTO> create(@Valid @RequestBody ReportStatusDTO dto){ return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto)); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportStatusResponseDTO> update(@PathVariable Long id,@Valid @RequestBody ReportStatusDTO dto){ return ResponseEntity.ok(service.update(id,dto)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
}
