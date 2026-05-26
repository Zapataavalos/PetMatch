package com.petmatch.region_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con la información de una región")
public record RegionResponseDto(

        @Schema(description = "Identificador único de la región", example = "1")
        Integer idRegion,

        @Schema(description = "Nombre de la región", example = "METROPOLITANA")
        String nombreRegion,

        @Schema(description = "ID del país asociado", example = "1")
        Integer idPais
) {
}