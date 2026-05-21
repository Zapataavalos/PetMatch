package com.petmatch.region_service.Event;

public record PaisEventDTO(
        Integer idPais,
        String nombrePais,
        Boolean activo
) {
}