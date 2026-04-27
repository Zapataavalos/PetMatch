package com.petmatch.msanimaltype.dto;

import lombok.*;

/**
 * DTO de respuesta que incluye el id.
 * Es el que consume ms-race a través de su Feign Client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalTypeResponseDTO {

    private Long idAnimalType;
    private String name;
}
