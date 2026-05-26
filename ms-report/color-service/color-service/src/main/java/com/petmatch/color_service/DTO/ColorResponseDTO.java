package com.petmatch.color_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con la información de un color")
public record ColorResponseDTO(

        @Schema(description = "Identificador único del color", example = "1")
        Integer idColor,

        @Schema(description = "Nombre del color", example = "ROJO")
        String nombreColor,

        @Schema(description = "Código hexadecimal del color", example = "#FF0000")
        String codigoHexadecimal

) {
}