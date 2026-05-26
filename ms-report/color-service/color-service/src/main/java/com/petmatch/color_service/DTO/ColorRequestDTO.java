package com.petmatch.color_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos requeridos para crear o actualizar un color")
public record ColorRequestDTO(

        @Schema(description = "Nombre del color", example = "Rojo")
        @NotBlank(message = "El nombre del color es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre del color debe tener entre 3 y 50 caracteres")
        String nombreColor,

        @Schema(description = "Código hexadecimal del color", example = "#FF0000")
        @NotBlank(message = "El código hexadecimal es obligatorio")
        @Pattern(
                regexp = "^#([A-Fa-f0-9]{6})$",
                message = "El código hexadecimal debe tener el formato #RRGGBB. Ejemplo: #FF0000"
        )
        String codigoHexadecimal

) {
}