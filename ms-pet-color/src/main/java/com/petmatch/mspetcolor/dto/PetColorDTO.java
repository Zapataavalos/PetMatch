package com.petmatch.mspetcolor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetColorDTO {

    @NotNull(message = "El id de la mascota es obligatorio")
    private Long idPet;

    @NotNull(message = "El id del color es obligatorio")
    private Long idColor;
}
