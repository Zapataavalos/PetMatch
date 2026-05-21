package com.petmatch.color_service.Event;

public record ColorEventDTO(
        Integer idColor,
        String nombreColor,
        String codigoHexadecimal,
        Boolean activo
) {
}