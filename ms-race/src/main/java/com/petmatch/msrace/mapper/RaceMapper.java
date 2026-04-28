package com.petmatch.msrace.mapper;

import com.petmatch.msrace.dto.RaceDTO;
import com.petmatch.msrace.model.Race;
import org.springframework.stereotype.Component;

@Component
public class RaceMapper {

    public Race toEntity(RaceDTO dto) {
        return Race.builder()
                .name(dto.getName())
                .idAnimalType(dto.getIdAnimalType())
                .build();
    }

    public RaceDTO toDTO(Race entity) {
        return RaceDTO.builder()
                .name(entity.getName())
                .idAnimalType(entity.getIdAnimalType())
                .build();
    }
}
