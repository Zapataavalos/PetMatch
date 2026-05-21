package com.petmatch.ubicacion_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con información de una ubicación")
public record UbicacionResponseDTO(

        @Schema(description = "Identificador único de la ubicación", example = "1")
        Integer idUbicacion,

        @Schema(description = "Dirección principal", example = "AV. PROVIDENCIA")
        String direccion,

        @Schema(description = "Número de la dirección", example = "1234")
        String numero,

        @Schema(description = "Referencia adicional", example = "CERCA DEL METRO")
        String referencia,

        @Schema(description = "Código postal", example = "7500000")
        String codigoPostal,

        @Schema(description = "Latitud", example = "-33.4263")
        Double latitud,

        @Schema(description = "Longitud", example = "-70.6170")
        Double longitud,

        @Schema(description = "ID de la ciudad asociada", example = "1")
        Integer idCiudad
) {
}