package com.petmatch.msreport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO de entrada para crear un reporte.
 * El cliente solo envía idType; el Factory Method asigna idStatus automáticamente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUser;

    @NotNull(message = "El id de la mascota es obligatorio")
    private Long idPet;

    @NotNull(message = "El id de la ubicación es obligatorio")
    private Long idLocation;

    @NotNull(message = "El tipo de reporte es obligatorio (1=PERDIDA, 2=ENCONTRADA, 3=EN_PELIGRO)")
    private Integer idType;
}
