package com.petmatch.mspetcolor.mapper;

import com.petmatch.mspetcolor.dto.ColorDTO;
import com.petmatch.mspetcolor.dto.ColorResponseDTO;
import com.petmatch.mspetcolor.model.Color;
import org.springframework.stereotype.Component;

@Component
public class ColorMapper {

    public Color toEntity(ColorDTO dto) {
        return Color.builder().name(dto.getName()).build();
    }

    public ColorResponseDTO toDTO(Color entity) {
        return ColorResponseDTO.builder()
                .idColor(entity.getIdColor())
                .name(entity.getName())
                .build();
    }
}
