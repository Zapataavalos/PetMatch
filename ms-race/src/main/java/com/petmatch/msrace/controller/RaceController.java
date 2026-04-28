package com.petmatch.msrace.controller;

import com.petmatch.msrace.dto.RaceDTO;
import com.petmatch.msrace.service.RaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/race")
@RequiredArgsConstructor
public class RaceController {

    private final RaceService raceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<RaceDTO>> getAll() {
        return ResponseEntity.ok(raceService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<RaceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(raceService.getById(id));
    }

    // Razas filtradas por tipo de animal (relación MER)
    @GetMapping("/animal-type/{idAnimalType}")
    @PreAuthorize("hasAnyRole('ADMIN','CIUDADANO','DUENO')")
    public ResponseEntity<List<RaceDTO>> getByAnimalType(@PathVariable Long idAnimalType) {
        return ResponseEntity.ok(raceService.getByAnimalType(idAnimalType));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RaceDTO> create(@Valid @RequestBody RaceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.save(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RaceDTO> update(@PathVariable Long id,
                                           @Valid @RequestBody RaceDTO dto) {
        return ResponseEntity.ok(raceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        raceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
