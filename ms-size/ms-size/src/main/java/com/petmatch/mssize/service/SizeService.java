package com.petmatch.mssize.service;

import com.petmatch.mssize.dto.SizeDTO;
import com.petmatch.mssize.dto.SizeResponseDTO;
import com.petmatch.mssize.exception.ResourceNotFoundException;
import com.petmatch.mssize.mapper.SizeMapper;
import com.petmatch.mssize.model.Size;
import com.petmatch.mssize.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;
    private final SizeMapper sizeMapper;

    public List<SizeResponseDTO> getAll() {
        return sizeRepository.findAll()
                .stream()
                .map(sizeMapper::toDTO)
                .toList();
    }

    public SizeResponseDTO getById(Long id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tamaño con id " + id + " no encontrado"));
        return sizeMapper.toDTO(size);
    }

    public SizeResponseDTO save(SizeDTO dto) {
        // Evitar duplicados por nombre
        sizeRepository.findByNameIgnoreCase(dto.getName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Ya existe un tamaño con el nombre: " + dto.getName());
                });
        return sizeMapper.toDTO(sizeRepository.save(sizeMapper.toEntity(dto)));
    }

    public SizeResponseDTO update(Long id, SizeDTO dto) {
        Size existing = sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tamaño con id " + id + " no encontrado"));
        existing.setName(dto.getName());
        return sizeMapper.toDTO(sizeRepository.save(existing));
    }

    public void delete(Long id) {
        sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tamaño con id " + id + " no encontrado"));
        sizeRepository.deleteById(id);
    }
}
