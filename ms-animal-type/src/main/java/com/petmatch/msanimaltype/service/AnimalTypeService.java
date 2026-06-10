package com.petmatch.msanimaltype.service;

import com.petmatch.msanimaltype.dto.AnimalTypeRequest;
import com.petmatch.msanimaltype.dto.AnimalTypeResponse;
import com.petmatch.msanimaltype.messaging.EventPublisher;
import com.petmatch.msanimaltype.model.AnimalType;
import com.petmatch.msanimaltype.repository.AnimalTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class AnimalTypeService {

    private final AnimalTypeRepository animalTypeRepository;
    private final EventPublisher eventPublisher;

    public AnimalTypeService(AnimalTypeRepository animalTypeRepository, EventPublisher eventPublisher) {
        this.animalTypeRepository = animalTypeRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<AnimalTypeResponse> listar() {
        return animalTypeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AnimalTypeResponse buscar(Long id) {
        return toResponse(findById(id));
    }

    public AnimalTypeResponse crear(AnimalTypeRequest request) {
        String nombre = normalizeName(request.nombre());
        if (animalTypeRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de animal duplicado");
        }

        AnimalType animalType = new AnimalType();
        apply(animalType, request);
        AnimalType saved = animalTypeRepository.save(animalType);
        AnimalTypeResponse response = toResponse(saved);
        eventPublisher.publish("CREATED", "ANIMAL_TYPE", response);
        return response;
    }

    public AnimalTypeResponse actualizar(Long id, AnimalTypeRequest request) {
        AnimalType animalType = findById(id);
        String nombre = normalizeName(request.nombre());
        if (animalTypeRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de animal duplicado");
        }

        apply(animalType, request);
        AnimalType saved = animalTypeRepository.save(animalType);
        AnimalTypeResponse response = toResponse(saved);
        eventPublisher.publish("UPDATED", "ANIMAL_TYPE", response);
        return response;
    }

    public void eliminar(Long id) {
        if (!animalTypeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de animal no encontrado");
        }

        animalTypeRepository.deleteById(id);
        eventPublisher.publish("DELETED", "ANIMAL_TYPE", Map.of("id", id));
    }

    private AnimalType findById(Long id) {
        return animalTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de animal no encontrado"));
    }

    private void apply(AnimalType animalType, AnimalTypeRequest request) {
        animalType.setNombre(normalizeName(request.nombre()));
        animalType.setDescripcion(normalizeText(request.descripcion(), "Sin descripcion"));
    }

    private AnimalTypeResponse toResponse(AnimalType animalType) {
        return new AnimalTypeResponse(animalType.getId(), animalType.getNombre(), animalType.getDescripcion());
    }

    private String normalizeName(String value) {
        return value.trim().toUpperCase();
    }

    private String normalizeText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }
}
