package com.petmatch.usuario_service.Event;

public record RolEventDTO(
        Integer idRol,
        String nombreRol,
        Boolean activo
) {
}