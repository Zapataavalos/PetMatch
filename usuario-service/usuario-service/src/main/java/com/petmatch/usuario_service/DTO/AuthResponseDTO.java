package com.petmatch.usuario_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta generada luego de una autenticación correcta")
public record AuthResponseDTO(

        @Schema(description = "Token JWT")
        String token,

        @Schema(description = "Tipo de token", example = "Bearer")
        String tipo,

        Integer idUsuario,
        String nombre,
        String email,
        Integer idRol
) {
}