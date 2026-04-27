package com.petmatch.mspet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDTO {

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUser;

    @NotNull(message = "El id de la raza es obligatorio")
    private Long idRace;

    @NotNull(message = "El id del tamaño es obligatorio")
    private Long idSize;
}
