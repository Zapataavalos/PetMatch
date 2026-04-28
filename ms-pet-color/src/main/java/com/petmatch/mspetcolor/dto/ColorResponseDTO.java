package com.petmatch.mspetcolor.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorResponseDTO {
    private Long idColor;
    private String name;
}
