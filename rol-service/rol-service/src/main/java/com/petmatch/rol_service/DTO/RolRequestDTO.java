package com.petmatch.rol_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos requeridos para crear o actualizar un rol")
public record RolRequestDTO(

        @Schema(
                description = "Nombre del rol dentro del sistema",
                example = "ADMINISTRADOR"
        )
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre del rol debe tener entre 3 y 50 caracteres")
        String nombreRol

) {
}