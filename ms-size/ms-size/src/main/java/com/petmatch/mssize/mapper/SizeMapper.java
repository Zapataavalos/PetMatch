package com.petmatch.mssize.mapper;

import com.petmatch.mssize.dto.SizeDTO;
import com.petmatch.mssize.dto.SizeResponseDTO;
import com.petmatch.mssize.model.Size;
import org.springframework.stereotype.Component;

@Component
public class SizeMapper {

    public Size toEntity(SizeDTO dto) {
        return Size.builder()
                .name(dto.getName())
                .build();
    }

    public SizeResponseDTO toDTO(Size entity) {
        return SizeResponseDTO.builder()
                .idSize(entity.getIdSize())
                .name(entity.getName())
                .build();
    }
}
