package com.petmatch.pais_service.Event;

public record PaisEventDTO(
        Integer idPais,
        String nombrePais,
        Boolean activo
) {
}