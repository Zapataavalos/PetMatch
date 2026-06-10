package com.petmatch.mspet.service;

import com.petmatch.mspet.dto.PetRequest;
import com.petmatch.mspet.dto.PetResponse;
import com.petmatch.mspet.messaging.PetEventPublisher;
import com.petmatch.mspet.model.Pet;
import com.petmatch.mspet.model.PetStatus;
import com.petmatch.mspet.repository.PetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class PetService implements PetOperations {

    private static final String DEFAULT_IMAGE_URL =
            "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600&auto=format&fit=crop";

    private final PetRepository petRepository;
    private final PetEventPublisher eventPublisher;

    public PetService(PetRepository petRepository, PetEventPublisher eventPublisher) {
        this.petRepository = petRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<PetResponse> listarMascotas() {
        return petRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PetResponse buscarMascota(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public PetResponse crearMascota(PetRequest request) {
        Pet pet = new Pet();
        applyRequest(pet, request);

        Pet saved = petRepository.save(pet);
        PetResponse response = toResponse(saved);
        eventPublisher.publish("CREATED", "PET", response);
        return response;
    }

    @Override
    public PetResponse actualizarMascota(Long id, PetRequest request) {
        Pet pet = findById(id);
        applyRequest(pet, request);

        Pet saved = petRepository.save(pet);
        PetResponse response = toResponse(saved);
        eventPublisher.publish("UPDATED", "PET", response);
        return response;
    }

    @Override
    public void eliminarMascota(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada");
        }

        petRepository.deleteById(id);
        eventPublisher.publish("DELETED", "PET", Map.of("id", id));
    }

    private Pet findById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mascota no encontrada"
                ));
    }

    private void applyRequest(Pet pet, PetRequest request) {
        pet.setNombre(normalizeText(request.nombre(), "Mascota sin nombre"));
        pet.setTipo(normalizeText(request.tipo(), "No informado"));
        pet.setRaza(normalizeText(request.raza(), "Mestizo"));
        pet.setTamano(normalizeText(request.tamano(), "Mediano"));
        pet.setEstado(request.estado() != null ? request.estado() : PetStatus.ACTIVO);
        pet.setDescripcion(normalizeText(request.descripcion(), "Sin descripcion"));
        pet.setImagenUrl(normalizeText(request.imagenUrl(), DEFAULT_IMAGE_URL));
    }

    private PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getNombre(),
                pet.getTipo(),
                pet.getRaza(),
                pet.getTamano(),
                pet.getEstado(),
                pet.getDescripcion(),
                pet.getImagenUrl(),
                pet.getCreatedAt()
        );
    }

    private String normalizeText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }
}
