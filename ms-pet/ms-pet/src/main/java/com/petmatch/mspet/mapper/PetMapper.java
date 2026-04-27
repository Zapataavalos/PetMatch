package com.petmatch.mspet.mapper;

import com.petmatch.mspet.dto.PetDTO;
import com.petmatch.mspet.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public Pet toEntity(PetDTO dto) {
        return Pet.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .idUser(dto.getIdUser())
                .idRace(dto.getIdRace())
                .idSize(dto.getIdSize())
                .build();
    }

    public PetDTO toDTO(Pet entity) {
        return PetDTO.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .idUser(entity.getIdUser())
                .idRace(entity.getIdRace())
                .idSize(entity.getIdSize())
                .build();
    }
}
