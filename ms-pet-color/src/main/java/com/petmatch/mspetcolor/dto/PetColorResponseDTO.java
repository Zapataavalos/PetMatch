package com.petmatch.mspetcolor.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetColorResponseDTO {
    private Long idPetColor;
    private Long idPet;
    private Long idColor;
    private String colorName;
}
