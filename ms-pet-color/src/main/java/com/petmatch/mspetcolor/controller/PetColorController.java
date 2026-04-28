package com.petmatch.mspetcolor.controller;

import com.petmatch.mspetcolor.dto.PetColorDTO;
import com.petmatch.mspetcolor.dto.PetColorResponseDTO;
import com.petmatch.mspetcolor.service.PetColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pet-color")
@RequiredArgsConstructor
public class PetColorController {

    private final PetColorService petColorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<PetColorResponseDTO>> getAll() {
        return ResponseEntity.ok(petColorService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<PetColorResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(petColorService.getById(id));
    }

    // Colores de una mascota específica (MER)
    @GetMapping("/pet/{idPet}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<PetColorResponseDTO>> getByPet(@PathVariable Long idPet) {
        return ResponseEntity.ok(petColorService.getByPet(idPet));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DUENO')")
    public ResponseEntity<PetColorResponseDTO> create(@Valid @RequestBody PetColorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petColorService.save(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DUENO')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        petColorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
