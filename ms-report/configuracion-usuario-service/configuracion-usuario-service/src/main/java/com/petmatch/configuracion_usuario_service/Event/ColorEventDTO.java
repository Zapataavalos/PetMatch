package com.petmatch.configuracion_usuario_service.Event;

public record ColorEventDTO(
        Integer idColor,
        String nombreColor,
        String codigoHexadecimal,
        Boolean activo
) {
}