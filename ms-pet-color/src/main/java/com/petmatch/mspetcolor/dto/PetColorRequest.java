package com.petmatch.mspetcolor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PetColorRequest(
        @NotNull(message = "La mascota es obligatoria")
        @Positive(message = "La mascota debe ser mayor a cero")
        Long petId,

        @NotNull(message = "El color es obligatorio")
        @Positive(message = "El color debe ser mayor a cero")
        Integer colorId,

        @NotBlank(message = "El nombre del color es obligatorio")
        @Size(max = 80, message = "El nombre del color no puede superar los 80 caracteres")
        String colorNombre,

        @NotBlank(message = "El codigo hexadecimal es obligatorio")
        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "El codigo hexadecimal debe tener formato #RRGGBB")
        String codigoHexadecimal
) {
}
