package com.petmatch.mspet.dto;

import com.petmatch.mspet.model.PetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PetRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El tipo es obligatorio")
        @Size(max = 60, message = "El tipo no puede superar los 60 caracteres")
        String tipo,

        @NotBlank(message = "La raza es obligatoria")
        @Size(max = 100, message = "La raza no puede superar los 100 caracteres")
        String raza,

        @NotBlank(message = "El tamano es obligatorio")
        @Size(max = 60, message = "El tamano no puede superar los 60 caracteres")
        String tamano,

        @NotNull(message = "El estado es obligatorio")
        PetStatus estado,

        @NotBlank(message = "La descripcion es obligatoria")
        @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
        String descripcion,

        String imagenUrl
) {
}
