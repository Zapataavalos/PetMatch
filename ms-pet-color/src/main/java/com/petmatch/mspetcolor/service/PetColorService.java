package com.petmatch.mspetcolor.service;

import com.petmatch.mspetcolor.client.PetClient;
import com.petmatch.mspetcolor.dto.PetColorDTO;
import com.petmatch.mspetcolor.dto.PetColorResponseDTO;
import com.petmatch.mspetcolor.exception.ResourceNotFoundException;
import com.petmatch.mspetcolor.mapper.PetColorMapper;
import com.petmatch.mspetcolor.model.Color;
import com.petmatch.mspetcolor.model.PetColor;
import com.petmatch.mspetcolor.repository.ColorRepository;
import com.petmatch.mspetcolor.repository.PetColorRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetColorService {

    private final PetColorRepository petColorRepository;
    private final ColorRepository colorRepository;
    private final PetColorMapper petColorMapper;
    private final PetClient petClient;

    public List<PetColorResponseDTO> getAll() {
        return petColorRepository.findAll().stream()
                .map(pc -> {
                    Color color = colorRepository.findById(pc.getIdColor()).orElse(null);
                    return petColorMapper.toDTO(pc, color);
                }).toList();
    }

    public List<PetColorResponseDTO> getByPet(Long idPet) {
        return petColorRepository.findByIdPet(idPet).stream()
                .map(pc -> {
                    Color color = colorRepository.findById(pc.getIdColor()).orElse(null);
                    return petColorMapper.toDTO(pc, color);
                }).toList();
    }

    public PetColorResponseDTO getById(Long id) {
        PetColor pc = petColorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PetColor con id " + id + " no encontrado"));
        Color color = colorRepository.findById(pc.getIdColor()).orElse(null);
        return petColorMapper.toDTO(pc, color);
    }

    public PetColorResponseDTO save(PetColorDTO dto) {
        // Validar que la mascota existe en ms-pet (MER)
        try {
            petClient.getById(dto.getIdPet());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Mascota con id " + dto.getIdPet() + " no existe en ms-pet");
        }

        // Validar que el color existe localmente
        colorRepository.findById(dto.getIdColor())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Color con id " + dto.getIdColor() + " no encontrado"));

        // Evitar duplicados
        if (petColorRepository.existsByIdPetAndIdColor(dto.getIdPet(), dto.getIdColor())) {
            throw new IllegalArgumentException("Esta mascota ya tiene ese color asignado");
        }

        PetColor saved = petColorRepository.save(petColorMapper.toEntity(dto));
        Color color = colorRepository.findById(saved.getIdColor()).orElse(null);
        return petColorMapper.toDTO(saved, color);
    }

    public void delete(Long id) {
        petColorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PetColor con id " + id + " no encontrado"));
        petColorRepository.deleteById(id);
    }
}
