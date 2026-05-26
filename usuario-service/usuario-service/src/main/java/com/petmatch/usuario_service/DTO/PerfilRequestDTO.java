package com.petmatch.usuario_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos editables del perfil del usuario autenticado")
public record PerfilRequestDTO(

        @Schema(description = "Nombre completo del usuario", example = "Benjamin Mendez")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @Schema(description = "Correo electronico del usuario", example = "benjamin@test.cl")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato valido")
        @Size(max = 120, message = "El email no puede superar los 120 caracteres")
        String email
) {
}
