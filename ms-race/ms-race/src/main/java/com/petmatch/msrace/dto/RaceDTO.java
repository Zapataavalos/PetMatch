package com.petmatch.msrace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceDTO {

    @NotBlank(message = "El nombre de la raza es obligatorio")
    private String name;

    @NotNull(message = "El id del tipo de animal es obligatorio")
    private Long idAnimalType;
}
