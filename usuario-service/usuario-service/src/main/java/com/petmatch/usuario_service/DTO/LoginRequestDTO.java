package com.petmatch.usuario_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales requeridas para iniciar sesión")
public record LoginRequestDTO(

        @Schema(description = "Correo electrónico del usuario", example = "benjamin@test.cl")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        @Schema(description = "Contraseña del usuario", example = "12345678")
        @NotBlank(message = "La contraseña es obligatoria")
        String contrasena
) {
}