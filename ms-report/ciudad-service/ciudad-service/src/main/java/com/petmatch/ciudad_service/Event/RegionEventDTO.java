package com.petmatch.ciudad_service.Event;

public record RegionEventDTO(
        Integer idRegion,
        String nombreRegion,
        Integer idPais,
        Boolean activo
) {
}