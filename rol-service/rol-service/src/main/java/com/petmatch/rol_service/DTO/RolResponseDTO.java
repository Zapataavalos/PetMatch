package com.petmatch.rol_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con la información de un rol")
public record RolResponseDTO(

        @Schema(description = "Identificador único del rol", example = "1")
        Integer idRol,

        @Schema(description = "Nombre del rol", example = "ADMINISTRADOR")
        String nombreRol

) {
}