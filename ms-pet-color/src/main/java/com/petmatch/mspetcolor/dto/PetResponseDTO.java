package com.petmatch.mspetcolor.dto;

import lombok.*;

/** Modela la respuesta de ms-pet para validar FK (MER). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseDTO {
    private Long idPet;
    private String name;
}
