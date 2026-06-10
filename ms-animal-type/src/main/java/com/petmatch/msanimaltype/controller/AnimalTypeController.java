package com.petmatch.msanimaltype.controller;

import com.petmatch.msanimaltype.dto.AnimalTypeRequest;
import com.petmatch.msanimaltype.dto.AnimalTypeResponse;
import com.petmatch.msanimaltype.service.AnimalTypeService;
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
@RequestMapping("/api/animal-type")
public class AnimalTypeController {

    private final AnimalTypeService animalTypeService;

    public AnimalTypeController(AnimalTypeService animalTypeService) {
        this.animalTypeService = animalTypeService;
    }

    @GetMapping
    public ResponseEntity<List<AnimalTypeResponse>> listar() {
        return ResponseEntity.ok(animalTypeService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalTypeResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(animalTypeService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<AnimalTypeResponse> crear(@Valid @RequestBody AnimalTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalTypeService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalTypeResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AnimalTypeRequest request
    ) {
        return ResponseEntity.ok(animalTypeService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        animalTypeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
