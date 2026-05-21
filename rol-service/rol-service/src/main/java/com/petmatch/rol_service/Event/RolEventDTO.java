package com.petmatch.rol_service.Event;

public record RolEventDTO(
        Integer idRol,
        String nombreRol,
        Boolean activo
) {
}