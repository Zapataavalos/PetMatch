package com.petmatch.ciudad_service.Event;

public record CiudadEventDTO(
        Integer idCiudad,
        String nombreCiudad,
        Integer idRegion,
        Boolean activo
) {
}