package com.petmatch.mssize.controller;

import com.petmatch.mssize.dto.SizeDTO;
import com.petmatch.mssize.dto.SizeResponseDTO;
import com.petmatch.mssize.service.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/size")
@RequiredArgsConstructor
public class SizeController {

    private final SizeService sizeService;

    // Expuesto a todos los autenticados
    // ms-pet lo consume via Feign para validar FK (MER)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<SizeResponseDTO>> getAll() {
        return ResponseEntity.ok(sizeService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<SizeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sizeService.getById(id));
    }

    // Solo ADMIN gestiona los tamaños
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SizeResponseDTO> create(@Valid @RequestBody SizeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sizeService.save(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SizeResponseDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody SizeDTO dto) {
        return ResponseEntity.ok(sizeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sizeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
