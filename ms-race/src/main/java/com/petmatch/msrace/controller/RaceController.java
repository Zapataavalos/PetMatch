package com.petmatch.msrace.controller;

import com.petmatch.msrace.dto.RaceRequest;
import com.petmatch.msrace.dto.RaceResponse;
import com.petmatch.msrace.service.RaceService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/race")
public class RaceController {

    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping
    public ResponseEntity<List<RaceResponse>> listar(@RequestParam(required = false) Long animalTypeId) {
        return ResponseEntity.ok(raceService.listar(animalTypeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(raceService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<RaceResponse> crear(@Valid @RequestBody RaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RaceResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RaceRequest request
    ) {
        return ResponseEntity.ok(raceService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        raceService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
