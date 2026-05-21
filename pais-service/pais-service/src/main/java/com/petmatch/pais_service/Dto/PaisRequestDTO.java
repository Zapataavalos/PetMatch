package com.petmatch.pais_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos requeridos para crear o actualizar un país")
public record PaisRequestDTO(

        @Schema(
                description = "Nombre del país",
                example = "Chile"
        )
        @NotBlank(message = "El nombre del país es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre del país debe tener entre 3 y 80 caracteres")
        String nombrePais

) {
}