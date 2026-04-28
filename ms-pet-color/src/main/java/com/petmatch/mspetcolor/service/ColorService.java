package com.petmatch.mspetcolor.service;

import com.petmatch.mspetcolor.dto.ColorDTO;
import com.petmatch.mspetcolor.dto.ColorResponseDTO;
import com.petmatch.mspetcolor.exception.ResourceNotFoundException;
import com.petmatch.mspetcolor.mapper.ColorMapper;
import com.petmatch.mspetcolor.model.Color;
import com.petmatch.mspetcolor.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorService {

    private final ColorRepository colorRepository;
    private final ColorMapper colorMapper;

    public List<ColorResponseDTO> getAll() {
        return colorRepository.findAll().stream().map(colorMapper::toDTO).toList();
    }

    public ColorResponseDTO getById(Long id) {
        return colorMapper.toDTO(colorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Color con id " + id + " no encontrado")));
    }

    public ColorResponseDTO save(ColorDTO dto) {
        colorRepository.findByNameIgnoreCase(dto.getName()).ifPresent(e -> {
            throw new IllegalArgumentException("Ya existe el color: " + dto.getName());
        });
        return colorMapper.toDTO(colorRepository.save(colorMapper.toEntity(dto)));
    }

    public ColorResponseDTO update(Long id, ColorDTO dto) {
        Color existing = colorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Color con id " + id + " no encontrado"));
        existing.setName(dto.getName());
        return colorMapper.toDTO(colorRepository.save(existing));
    }

    public void delete(Long id) {
        colorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Color con id " + id + " no encontrado"));
        colorRepository.deleteById(id);
    }
}
