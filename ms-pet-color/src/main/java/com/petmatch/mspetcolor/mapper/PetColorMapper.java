package com.petmatch.mspetcolor.mapper;

import com.petmatch.mspetcolor.dto.PetColorDTO;
import com.petmatch.mspetcolor.dto.PetColorResponseDTO;
import com.petmatch.mspetcolor.model.Color;
import com.petmatch.mspetcolor.model.PetColor;
import org.springframework.stereotype.Component;

@Component
public class PetColorMapper {

    public PetColor toEntity(PetColorDTO dto) {
        return PetColor.builder()
                .idPet(dto.getIdPet())
                .idColor(dto.getIdColor())
                .build();
    }

    public PetColorResponseDTO toDTO(PetColor entity, Color color) {
        return PetColorResponseDTO.builder()
                .idPetColor(entity.getIdPetColor())
                .idPet(entity.getIdPet())
                .idColor(entity.getIdColor())
                .colorName(color != null ? color.getName() : null)
                .build();
    }
}
