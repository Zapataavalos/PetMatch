package com.petmatch.msanimaltype.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnimalTypeRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
        String nombre,

        @Size(max = 200, message = "La descripcion no puede superar los 200 caracteres")
        String descripcion
) {
}
