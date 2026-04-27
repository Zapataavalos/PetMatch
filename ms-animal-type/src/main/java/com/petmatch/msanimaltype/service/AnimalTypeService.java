package com.petmatch.msanimaltype.service;

import com.petmatch.msanimaltype.dto.AnimalTypeDTO;
import com.petmatch.msanimaltype.dto.AnimalTypeResponseDTO;
import com.petmatch.msanimaltype.exception.ResourceNotFoundException;
import com.petmatch.msanimaltype.mapper.AnimalTypeMapper;
import com.petmatch.msanimaltype.model.AnimalType;
import com.petmatch.msanimaltype.repository.AnimalTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalTypeService {

    private final AnimalTypeRepository animalTypeRepository;
    private final AnimalTypeMapper animalTypeMapper;

    public List<AnimalTypeResponseDTO> getAll() {
        return animalTypeRepository.findAll()
                .stream()
                .map(animalTypeMapper::toDTO)
                .toList();
    }

    public AnimalTypeResponseDTO getById(Long id) {
        AnimalType animalType = animalTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de animal con id " + id + " no encontrado"));
        return animalTypeMapper.toDTO(animalType);
    }

    public AnimalTypeResponseDTO save(AnimalTypeDTO dto) {
        // Evitar duplicados por nombre
        animalTypeRepository.findByNameIgnoreCase(dto.getName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Ya existe un tipo de animal con el nombre: " + dto.getName());
                });
        AnimalType saved = animalTypeRepository.save(animalTypeMapper.toEntity(dto));
        return animalTypeMapper.toDTO(saved);
    }

    public AnimalTypeResponseDTO update(Long id, AnimalTypeDTO dto) {
        AnimalType existing = animalTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de animal con id " + id + " no encontrado"));
        existing.setName(dto.getName());
        return animalTypeMapper.toDTO(animalTypeRepository.save(existing));
    }

    public void delete(Long id) {
        animalTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de animal con id " + id + " no encontrado"));
        animalTypeRepository.deleteById(id);
    }
}
