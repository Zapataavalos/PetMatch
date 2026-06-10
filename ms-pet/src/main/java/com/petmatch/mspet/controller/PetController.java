package com.petmatch.mspet.controller;

import com.petmatch.mspet.dto.PetRequest;
import com.petmatch.mspet.dto.PetResponse;
import com.petmatch.mspet.service.PetOperations;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    private final PetOperations petService;

    public PetController(PetOperations petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> listarMascotas() {
        return ResponseEntity.ok(petService.listarMascotas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> buscarMascota(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarMascota(id));
    }

    @PostMapping
    public ResponseEntity<PetResponse> crearMascota(@Valid @RequestBody PetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.crearMascota(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> actualizarMascota(
            @PathVariable Long id,
            @Valid @RequestBody PetRequest request
    ) {
        return ResponseEntity.ok(petService.actualizarMascota(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        petService.eliminarMascota(id);
        return ResponseEntity.noContent().build();
    }
}
