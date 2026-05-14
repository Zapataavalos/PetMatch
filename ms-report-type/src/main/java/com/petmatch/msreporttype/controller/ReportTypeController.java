package com.petmatch.msreporttype.controller;
import com.petmatch.msreporttype.dto.ReportTypeDTO;
import com.petmatch.msreporttype.dto.ReportTypeResponseDTO;
import com.petmatch.msreporttype.service.ReportTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/report-type") @RequiredArgsConstructor
public class ReportTypeController {
    private final ReportTypeService service;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<ReportTypeResponseDTO>> getAll(){ return ResponseEntity.ok(service.getAll()); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<ReportTypeResponseDTO> getById(@PathVariable Long id){ return ResponseEntity.ok(service.getById(id)); }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportTypeResponseDTO> create(@Valid @RequestBody ReportTypeDTO dto){ return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto)); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportTypeResponseDTO> update(@PathVariable Long id,@Valid @RequestBody ReportTypeDTO dto){ return ResponseEntity.ok(service.update(id,dto)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
}
