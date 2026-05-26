package com.petmatch.pais_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con la información de un país")
public record PaisResponseDTO(

        @Schema(description = "Identificador único del país", example = "1")
        Integer idPais,

        @Schema(description = "Nombre del país", example = "CHILE")
        String nombrePais

) {
}