package com.petmatch.region_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos requeridos para crear o actualizar una región")
public record RegionRequestDto(

        @Schema(description = "Nombre de la región", example = "Metropolitana")
        @NotBlank(message = "El nombre de la región es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre de la región debe tener entre 3 y 100 caracteres")
        String nombreRegion,

        @Schema(description = "ID del país asociado", example = "1")
        @NotNull(message = "El ID del país es obligatorio")
        @Positive(message = "El ID del país debe ser mayor a cero")
        Integer idPais
) {
}