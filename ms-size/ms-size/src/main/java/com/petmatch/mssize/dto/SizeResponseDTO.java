package com.petmatch.mssize.dto;

import lombok.*;

/**
 * DTO de respuesta que incluye el id.
 * Es el que consume ms-pet a través de su Feign Client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeResponseDTO {

    private Long idSize;
    private String name;
}
