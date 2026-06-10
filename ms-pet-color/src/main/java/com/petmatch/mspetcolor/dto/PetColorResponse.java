package com.petmatch.mspetcolor.dto;

public record PetColorResponse(
        Long id,
        Long petId,
        Integer colorId,
        String colorNombre,
        String codigoHexadecimal
) {
}
