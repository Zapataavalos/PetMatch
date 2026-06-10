package com.petmatch.msrace.service;

import com.petmatch.msrace.dto.RaceRequest;
import com.petmatch.msrace.dto.RaceResponse;
import com.petmatch.msrace.messaging.EventPublisher;
import com.petmatch.msrace.model.Race;
import com.petmatch.msrace.repository.RaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class RaceService {

    private final RaceRepository raceRepository;
    private final EventPublisher eventPublisher;

    public RaceService(RaceRepository raceRepository, EventPublisher eventPublisher) {
        this.raceRepository = raceRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<RaceResponse> listar(Long animalTypeId) {
        List<Race> races = animalTypeId == null
                ? raceRepository.findAll()
                : raceRepository.findByAnimalTypeIdOrderByNombreAsc(animalTypeId);

        return races.stream().map(this::toResponse).toList();
    }

    public RaceResponse buscar(Long id) {
        return toResponse(findById(id));
    }

    public RaceResponse crear(RaceRequest request) {
        String nombre = normalizeName(request.nombre());
        if (raceRepository.existsByNombreIgnoreCaseAndAnimalTypeId(nombre, request.animalTypeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Raza duplicada para el tipo de animal");
        }

        Race race = new Race();
        apply(race, request);
        Race saved = raceRepository.save(race);
        RaceResponse response = toResponse(saved);
        eventPublisher.publish("CREATED", "RACE", response);
        return response;
    }

    public RaceResponse actualizar(Long id, RaceRequest request) {
        Race race = findById(id);
        String nombre = normalizeName(request.nombre());
        if (raceRepository.existsByNombreIgnoreCaseAndAnimalTypeIdAndIdNot(nombre, request.animalTypeId(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Raza duplicada para el tipo de animal");
        }

        apply(race, request);
        Race saved = raceRepository.save(race);
        RaceResponse response = toResponse(saved);
        eventPublisher.publish("UPDATED", "RACE", response);
        return response;
    }

    public void eliminar(Long id) {
        if (!raceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Raza no encontrada");
        }

        raceRepository.deleteById(id);
        eventPublisher.publish("DELETED", "RACE", Map.of("id", id));
    }

    private Race findById(Long id) {
        return raceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Raza no encontrada"));
    }

    private void apply(Race race, RaceRequest request) {
        race.setNombre(normalizeName(request.nombre()));
        race.setAnimalTypeId(request.animalTypeId());
        race.setAnimalTypeNombre(normalizeName(request.animalTypeNombre()));
    }

    private RaceResponse toResponse(Race race) {
        return new RaceResponse(
                race.getId(),
                race.getNombre(),
                race.getAnimalTypeId(),
                race.getAnimalTypeNombre()
        );
    }

    private String normalizeName(String value) {
        return value.trim().toUpperCase();
    }
}
