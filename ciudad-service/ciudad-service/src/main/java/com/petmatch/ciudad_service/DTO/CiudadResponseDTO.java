package com.petmatch.ciudad_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con la información de una ciudad")
public record CiudadResponseDTO(

        @Schema(description = "Identificador único de la ciudad", example = "1")
        Integer idCiudad,

        @Schema(description = "Nombre de la ciudad", example = "SANTIAGO")
        String nombreCiudad,

        @Schema(description = "ID de la región asociada", example = "1")
        Integer idRegion
) {
}