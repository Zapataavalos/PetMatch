package com.petmatch.region_service.Event;

public record RegionEventDTO(
        Integer idRegion,
        String nombreRegion,
        Integer idPais,
        Boolean activo
) {
}