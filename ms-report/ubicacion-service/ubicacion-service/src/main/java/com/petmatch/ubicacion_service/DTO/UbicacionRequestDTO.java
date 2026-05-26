package com.petmatch.ubicacion_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos requeridos para crear o actualizar una ubicación")
public record UbicacionRequestDTO(

        @Schema(description = "Dirección principal", example = "Av. Providencia")
        @NotBlank(message = "La dirección es obligatoria")
        @Size(min = 3, max = 150, message = "La dirección debe tener entre 3 y 150 caracteres")
        String direccion,

        @Schema(description = "Número de la dirección", example = "1234")
        @NotBlank(message = "El número es obligatorio")
        @Size(max = 20, message = "El número no puede superar los 20 caracteres")
        String numero,

        @Schema(description = "Referencia adicional de la ubicación", example = "Cerca del metro")
        @Size(max = 150, message = "La referencia no puede superar los 150 caracteres")
        String referencia,

        @Schema(description = "Código postal", example = "7500000")
        @Size(max = 15, message = "El código postal no puede superar los 15 caracteres")
        String codigoPostal,

        @Schema(description = "Latitud geográfica", example = "-33.4263")
        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90.0", message = "La latitud mínima permitida es -90")
        @DecimalMax(value = "90.0", message = "La latitud máxima permitida es 90")
        Double latitud,

        @Schema(description = "Longitud geográfica", example = "-70.6170")
        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180.0", message = "La longitud mínima permitida es -180")
        @DecimalMax(value = "180.0", message = "La longitud máxima permitida es 180")
        Double longitud,

        @Schema(description = "ID de la ciudad asociada", example = "1")
        @NotNull(message = "El ID de la ciudad es obligatorio")
        @Positive(message = "El ID de la ciudad debe ser mayor a cero")
        Integer idCiudad
) {
}