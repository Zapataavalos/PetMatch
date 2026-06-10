package com.petmatch.mssize.service;

import com.petmatch.mssize.dto.SizeRequest;
import com.petmatch.mssize.dto.SizeResponse;
import com.petmatch.mssize.messaging.EventPublisher;
import com.petmatch.mssize.model.PetSize;
import com.petmatch.mssize.repository.PetSizeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class SizeService {

    private final PetSizeRepository petSizeRepository;
    private final EventPublisher eventPublisher;

    public SizeService(PetSizeRepository petSizeRepository, EventPublisher eventPublisher) {
        this.petSizeRepository = petSizeRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<SizeResponse> listar() {
        return petSizeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SizeResponse buscar(Long id) {
        return toResponse(findById(id));
    }

    public SizeResponse crear(SizeRequest request) {
        String nombre = normalizeName(request.nombre());
        if (petSizeRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tamano duplicado");
        }

        PetSize petSize = new PetSize();
        apply(petSize, request);
        PetSize saved = petSizeRepository.save(petSize);
        SizeResponse response = toResponse(saved);
        eventPublisher.publish("CREATED", "SIZE", response);
        return response;
    }

    public SizeResponse actualizar(Long id, SizeRequest request) {
        PetSize petSize = findById(id);
        String nombre = normalizeName(request.nombre());
        if (petSizeRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tamano duplicado");
        }

        apply(petSize, request);
        PetSize saved = petSizeRepository.save(petSize);
        SizeResponse response = toResponse(saved);
        eventPublisher.publish("UPDATED", "SIZE", response);
        return response;
    }

    public void eliminar(Long id) {
        if (!petSizeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tamano no encontrado");
        }

        petSizeRepository.deleteById(id);
        eventPublisher.publish("DELETED", "SIZE", Map.of("id", id));
    }

    private PetSize findById(Long id) {
        return petSizeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tamano no encontrado"));
    }

    private void apply(PetSize petSize, SizeRequest request) {
        petSize.setNombre(normalizeName(request.nombre()));
        petSize.setDescripcion(normalizeText(request.descripcion(), "Sin descripcion"));
    }

    private SizeResponse toResponse(PetSize petSize) {
        return new SizeResponse(petSize.getId(), petSize.getNombre(), petSize.getDescripcion());
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
