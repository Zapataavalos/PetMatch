package com.petmatch.configuracion_usuario_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con la configuración de usuario")
public record ConfiguracionUsuarioResponseDTO(

        @Schema(description = "ID de la configuración", example = "1")
        Integer idConfiguracionUsuario,

        @Schema(description = "ID del usuario asociado", example = "1")
        Integer idUsuario,

        @Schema(description = "ID del color asociado", example = "1")
        Integer idColor,

        @Schema(description = "Notificaciones activas", example = "true")
        Boolean notificacionesActivas,

        @Schema(description = "Modo oscuro activo", example = "false")
        Boolean modoOscuro,

        @Schema(description = "Idioma configurado", example = "ES")
        String idioma
) {
}