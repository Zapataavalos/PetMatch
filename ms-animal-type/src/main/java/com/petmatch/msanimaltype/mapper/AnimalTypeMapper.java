package com.petmatch.msanimaltype.mapper;

import com.petmatch.msanimaltype.dto.AnimalTypeDTO;
import com.petmatch.msanimaltype.dto.AnimalTypeResponseDTO;
import com.petmatch.msanimaltype.model.AnimalType;
import org.springframework.stereotype.Component;

@Component
public class AnimalTypeMapper {

    public AnimalType toEntity(AnimalTypeDTO dto) {
        return AnimalType.builder()
                .name(dto.getName())
                .build();
    }

    public AnimalTypeResponseDTO toDTO(AnimalType entity) {
        return AnimalTypeResponseDTO.builder()
                .idAnimalType(entity.getIdAnimalType())
                .name(entity.getName())
                .build();
    }
}
