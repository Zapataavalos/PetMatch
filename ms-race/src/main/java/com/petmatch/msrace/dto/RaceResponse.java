package com.petmatch.msrace.dto;

public record RaceResponse(
        Long id,
        String nombre,
        Long animalTypeId,
        String animalTypeNombre
) {
}
