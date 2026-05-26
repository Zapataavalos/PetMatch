package com.petmatch.usuario_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos requeridos para crear o actualizar un usuario")
public record UsuarioRequestDTO(

        @Schema(description = "Nombre completo del usuario", example = "Benjamin Mendez")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @Schema(description = "Correo electrónico del usuario", example = "benjamin@test.cl")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 120, message = "El email no puede superar los 120 caracteres")
        String email,

        @Schema(description = "Contraseña del usuario", example = "12345678")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 60, message = "La contraseña debe tener entre 8 y 60 caracteres")
        String contrasena,

        @Schema(description = "ID del rol asociado", example = "1")
        @NotNull(message = "El ID del rol es obligatorio")
        @Positive(message = "El ID del rol debe ser mayor a cero")
        Integer idRol
) {
}