package com.petmatch.ubicacion_service.Event;

public record CiudadEventDTO(
        Integer idCiudad,
        String nombreCiudad,
        Integer idRegion,
        Boolean activo
) {
}