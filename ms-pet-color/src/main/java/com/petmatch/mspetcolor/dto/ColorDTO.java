package com.petmatch.mspetcolor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorDTO {

    @NotBlank(message = "El nombre del color es obligatorio")
    private String name;
}
