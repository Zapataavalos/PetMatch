package com.petmatch.configuracion_usuario_service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos requeridos para crear o actualizar una configuración de usuario")
public record ConfiguracionUsuarioRequestDTO(

        @Schema(description = "ID del usuario asociado", example = "1")
        @NotNull(message = "El ID del usuario es obligatorio")
        @Positive(message = "El ID del usuario debe ser mayor a cero")
        Integer idUsuario,

        @Schema(description = "ID del color asociado", example = "1")
        @NotNull(message = "El ID del color es obligatorio")
        @Positive(message = "El ID del color debe ser mayor a cero")
        Integer idColor,

        @Schema(description = "Indica si las notificaciones están activas", example = "true")
        @NotNull(message = "El estado de notificaciones es obligatorio")
        Boolean notificacionesActivas,

        @Schema(description = "Indica si el modo oscuro está activo", example = "false")
        @NotNull(message = "El estado del modo oscuro es obligatorio")
        Boolean modoOscuro,

        @Schema(description = "Idioma de la configuración", example = "ES")
        @NotBlank(message = "El idioma es obligatorio")
        @Pattern(regexp = "^(ES|EN)$", message = "El idioma debe ser ES o EN")
        String idioma
) {
}