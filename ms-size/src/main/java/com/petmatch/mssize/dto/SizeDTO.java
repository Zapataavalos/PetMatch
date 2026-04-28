package com.petmatch.mssize.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeDTO {

    @NotBlank(message = "El nombre del tamaño es obligatorio")
    private String name;
}
