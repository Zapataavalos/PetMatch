package com.petmatch.msrace.dto;

import lombok.*;

/**
 * DTO que modela la respuesta de ms-animal-type.
 * Solo se usan los campos necesarios para validar la FK.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalTypeResponseDTO {
    private Long idAnimalType;
    private String name;
}
