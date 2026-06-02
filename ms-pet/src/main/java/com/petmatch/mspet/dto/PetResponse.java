package com.petmatch.mspet.dto;

import com.petmatch.mspet.model.PetStatus;

import java.time.LocalDateTime;

public record PetResponse(
        Long id,
        String nombre,
        String tipo,
        String raza,
        String tamano,
        PetStatus estado,
        String descripcion,
        String imagenUrl,
        LocalDateTime createdAt
) {
}
