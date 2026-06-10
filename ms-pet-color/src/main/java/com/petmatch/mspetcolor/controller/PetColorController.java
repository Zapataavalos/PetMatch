package com.petmatch.mspetcolor.controller;

import com.petmatch.mspetcolor.dto.PetColorRequest;
import com.petmatch.mspetcolor.dto.PetColorResponse;
import com.petmatch.mspetcolor.service.PetColorService;
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
@RequestMapping({"/api/pet-color", "/api/color"})
public class PetColorController {

    private final PetColorService petColorService;

    public PetColorController(PetColorService petColorService) {
        this.petColorService = petColorService;
    }

    @GetMapping
    public ResponseEntity<List<PetColorResponse>> listar(@RequestParam(required = false) Long petId) {
        return ResponseEntity.ok(petColorService.listar(petId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetColorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(petColorService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<PetColorResponse> crear(@Valid @RequestBody PetColorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petColorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetColorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PetColorRequest request
    ) {
        return ResponseEntity.ok(petColorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        petColorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
