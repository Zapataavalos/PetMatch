package com.petmatch.msrace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RaceRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotNull(message = "El tipo de animal es obligatorio")
        @Positive(message = "El tipo de animal debe ser mayor a cero")
        Long animalTypeId,

        @NotBlank(message = "El nombre del tipo de animal es obligatorio")
        @Size(max = 80, message = "El nombre del tipo de animal no puede superar los 80 caracteres")
        String animalTypeNombre
) {
}
