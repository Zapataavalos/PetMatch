package com.petmatch.usuario_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Perfil actualizado con token vigente")
public record PerfilResponseDTO(

        @Schema(description = "Token JWT actualizado")
        String token,

        @Schema(description = "Tipo de token", example = "Bearer")
        String tipo,

        Integer idUsuario,
        String nombre,
        String email,
        Integer idRol,
        LocalDateTime fechaRegistro
) {
}
