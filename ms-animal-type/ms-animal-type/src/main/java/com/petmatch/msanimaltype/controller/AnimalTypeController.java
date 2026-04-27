package com.petmatch.msanimaltype.controller;

import com.petmatch.msanimaltype.dto.AnimalTypeDTO;
import com.petmatch.msanimaltype.dto.AnimalTypeResponseDTO;
import com.petmatch.msanimaltype.service.AnimalTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animal-type")
@RequiredArgsConstructor
public class AnimalTypeController {

    private final AnimalTypeService animalTypeService;

    // Expuesto a todos los autenticados
    // ms-race también lo consume via Feign para validar FK
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<AnimalTypeResponseDTO>> getAll() {
        return ResponseEntity.ok(animalTypeService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<AnimalTypeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(animalTypeService.getById(id));
    }

    // Solo ADMIN gestiona los tipos de animales
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnimalTypeResponseDTO> create(@Valid @RequestBody AnimalTypeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalTypeService.save(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnimalTypeResponseDTO> update(@PathVariable Long id,
                                                         @Valid @RequestBody AnimalTypeDTO dto) {
        return ResponseEntity.ok(animalTypeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        animalTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
