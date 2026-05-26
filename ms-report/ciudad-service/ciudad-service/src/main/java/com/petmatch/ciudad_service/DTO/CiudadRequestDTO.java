package com.petmatch.ciudad_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos requeridos para crear o actualizar una ciudad")
public record CiudadRequestDTO(

        @Schema(description = "Nombre de la ciudad", example = "Santiago")
        @NotBlank(message = "El nombre de la ciudad es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre de la ciudad debe tener entre 3 y 100 caracteres")
        String nombreCiudad,

        @Schema(description = "ID de la región asociada", example = "1")
        @NotNull(message = "El ID de la región es obligatorio")
        @Positive(message = "El ID de la región debe ser mayor a cero")
        Integer idRegion
) {
}