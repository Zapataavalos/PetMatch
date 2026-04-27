package com.petmatch.mspet.controller;

import com.petmatch.mspet.dto.PetDTO;
import com.petmatch.mspet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<PetDTO>> getAll() {
        return ResponseEntity.ok(petService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<PetDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getById(id));
    }

    @GetMapping("/user/{idUser}")
    @PreAuthorize("hasAnyRole('ADMIN','DUENO')")
    public ResponseEntity<List<PetDTO>> getByUser(@PathVariable Long idUser) {
        return ResponseEntity.ok(petService.getByUser(idUser));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DUENO')")
    public ResponseEntity<PetDTO> create(@Valid @RequestBody PetDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.save(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DUENO')")
    public ResponseEntity<PetDTO> update(@PathVariable Long id,
                                          @Valid @RequestBody PetDTO dto) {
        return ResponseEntity.ok(petService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
