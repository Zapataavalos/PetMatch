package com.petmatch.msanimaltype.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalTypeDTO {

    @NotBlank(message = "El nombre del tipo de animal es obligatorio")
    private String name;
}
