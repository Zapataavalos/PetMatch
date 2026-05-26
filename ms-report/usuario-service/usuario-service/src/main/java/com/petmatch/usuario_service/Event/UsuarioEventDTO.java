package com.petmatch.usuario_service.Event;

public record UsuarioEventDTO(
        Integer idUsuario,
        String nombre,
        String email,
        Integer idRol,
        Boolean activo
) {
}