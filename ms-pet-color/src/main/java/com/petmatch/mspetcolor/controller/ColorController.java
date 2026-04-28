package com.petmatch.mspetcolor.controller;

import com.petmatch.mspetcolor.dto.ColorDTO;
import com.petmatch.mspetcolor.dto.ColorResponseDTO;
import com.petmatch.mspetcolor.service.ColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/color")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<ColorResponseDTO>> getAll() {
        return ResponseEntity.ok(colorService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<ColorResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(colorService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColorResponseDTO> create(@Valid @RequestBody ColorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colorService.save(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColorResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody ColorDTO dto) {
        return ResponseEntity.ok(colorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        colorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
