package com.petmatch.mspet.service;

import com.petmatch.mspet.dto.PetDTO;
import com.petmatch.mspet.exception.ResourceNotFoundException;
import com.petmatch.mspet.mapper.PetMapper;
import com.petmatch.mspet.model.Pet;
import com.petmatch.mspet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    public List<PetDTO> getAll() {
        return petRepository.findAll()
                .stream()
                .map(petMapper::toDTO)
                .toList();
    }

    public PetDTO getById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mascota con id " + id + " no encontrada"));
        return petMapper.toDTO(pet);
    }

    public List<PetDTO> getByUser(Long idUser) {
        return petRepository.findByIdUser(idUser)
                .stream()
                .map(petMapper::toDTO)
                .toList();
    }

    public PetDTO save(PetDTO dto) {
        return petMapper.toDTO(petRepository.save(petMapper.toEntity(dto)));
    }

    public PetDTO update(Long id, PetDTO dto) {
        petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mascota con id " + id + " no encontrada"));
        Pet pet = petMapper.toEntity(dto);
        pet.setIdPet(id);
        return petMapper.toDTO(petRepository.save(pet));
    }

    public void delete(Long id) {
        petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mascota con id " + id + " no encontrada"));
        petRepository.deleteById(id);
    }
}
