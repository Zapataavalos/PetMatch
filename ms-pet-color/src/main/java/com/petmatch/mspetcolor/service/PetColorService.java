package com.petmatch.mspetcolor.service;

import com.petmatch.mspetcolor.dto.PetColorRequest;
import com.petmatch.mspetcolor.dto.PetColorResponse;
import com.petmatch.mspetcolor.messaging.EventPublisher;
import com.petmatch.mspetcolor.model.PetColor;
import com.petmatch.mspetcolor.repository.PetColorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class PetColorService {

    private final PetColorRepository petColorRepository;
    private final EventPublisher eventPublisher;

    public PetColorService(PetColorRepository petColorRepository, EventPublisher eventPublisher) {
        this.petColorRepository = petColorRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<PetColorResponse> listar(Long petId) {
        List<PetColor> colors = petId == null
                ? petColorRepository.findAll()
                : petColorRepository.findByPetIdOrderByColorNombreAsc(petId);

        return colors.stream().map(this::toResponse).toList();
    }

    public PetColorResponse buscar(Long id) {
        return toResponse(findById(id));
    }

    public PetColorResponse crear(PetColorRequest request) {
        if (petColorRepository.existsByPetIdAndColorId(request.petId(), request.colorId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Color ya asociado a la mascota");
        }

        PetColor petColor = new PetColor();
        apply(petColor, request);
        PetColor saved = petColorRepository.save(petColor);
        PetColorResponse response = toResponse(saved);
        eventPublisher.publish("CREATED", "PET_COLOR", response);
        return response;
    }

    public PetColorResponse actualizar(Long id, PetColorRequest request) {
        PetColor petColor = findById(id);
        if (petColorRepository.existsByPetIdAndColorIdAndIdNot(request.petId(), request.colorId(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Color ya asociado a la mascota");
        }

        apply(petColor, request);
        PetColor saved = petColorRepository.save(petColor);
        PetColorResponse response = toResponse(saved);
        eventPublisher.publish("UPDATED", "PET_COLOR", response);
        return response;
    }

    public void eliminar(Long id) {
        if (!petColorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Color de mascota no encontrado");
        }

        petColorRepository.deleteById(id);
        eventPublisher.publish("DELETED", "PET_COLOR", Map.of("id", id));
    }

    private PetColor findById(Long id) {
        return petColorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Color de mascota no encontrado"));
    }

    private void apply(PetColor petColor, PetColorRequest request) {
        petColor.setPetId(request.petId());
        petColor.setColorId(request.colorId());
        petColor.setColorNombre(request.colorNombre().trim().toUpperCase());
        petColor.setCodigoHexadecimal(request.codigoHexadecimal().trim().toUpperCase());
    }

    private PetColorResponse toResponse(PetColor petColor) {
        return new PetColorResponse(
                petColor.getId(),
                petColor.getPetId(),
                petColor.getColorId(),
                petColor.getColorNombre(),
                petColor.getCodigoHexadecimal()
        );
    }
}
