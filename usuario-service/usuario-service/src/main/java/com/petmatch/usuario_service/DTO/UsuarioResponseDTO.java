package com.petmatch.usuario_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta con la información pública de un usuario")
public record UsuarioResponseDTO(

        @Schema(description = "Identificador único del usuario", example = "1")
        Integer idUsuario,

        @Schema(description = "Nombre del usuario", example = "BENJAMIN MENDEZ")
        String nombre,

        @Schema(description = "Email del usuario", example = "benjamin@test.cl")
        String email,

        @Schema(description = "Fecha de registro del usuario")
        LocalDateTime fechaRegistro,

        @Schema(description = "ID del rol asociado", example = "1")
        Integer idRol
) {
}