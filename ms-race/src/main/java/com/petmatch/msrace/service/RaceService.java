package com.petmatch.msrace.service;

import com.petmatch.msrace.client.AnimalTypeClient;
import com.petmatch.msrace.dto.RaceDTO;
import com.petmatch.msrace.exception.ResourceNotFoundException;
import com.petmatch.msrace.mapper.RaceMapper;
import com.petmatch.msrace.model.Race;
import com.petmatch.msrace.repository.RaceRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceService {

    private final RaceRepository raceRepository;
    private final RaceMapper raceMapper;
    private final AnimalTypeClient animalTypeClient;

    public List<RaceDTO> getAll() {
        return raceRepository.findAll()
                .stream()
                .map(raceMapper::toDTO)
                .toList();
    }

    public RaceDTO getById(Long id) {
        Race race = raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Raza con id " + id + " no encontrada"));
        return raceMapper.toDTO(race);
    }

    public List<RaceDTO> getByAnimalType(Long idAnimalType) {
        return raceRepository.findByIdAnimalType(idAnimalType)
                .stream()
                .map(raceMapper::toDTO)
                .toList();
    }

    public RaceDTO save(RaceDTO dto) {
        // Valida que el idAnimalType exista en ms-animal-type (MER)
        validateAnimalType(dto.getIdAnimalType());
        return raceMapper.toDTO(raceRepository.save(raceMapper.toEntity(dto)));
    }

    public RaceDTO update(Long id, RaceDTO dto) {
        raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Raza con id " + id + " no encontrada"));
        validateAnimalType(dto.getIdAnimalType());
        Race race = raceMapper.toEntity(dto);
        race.setIdRace(id);
        return raceMapper.toDTO(raceRepository.save(race));
    }

    public void delete(Long id) {
        raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Raza con id " + id + " no encontrada"));
        raceRepository.deleteById(id);
    }

    /**
     * Llama a ms-animal-type para verificar que el tipo de animal existe.
     * Si el servicio no responde o devuelve 404 → lanza excepción.
     */
    private void validateAnimalType(Long idAnimalType) {
        try {
            animalTypeClient.getById(idAnimalType);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Tipo de animal con id " + idAnimalType + " no existe en ms-animal-type");
        } catch (FeignException e) {
            throw new RuntimeException(
                    "Error al conectar con ms-animal-type: " + e.getMessage());
        }
    }
}
