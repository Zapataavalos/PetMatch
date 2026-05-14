package com.petmatch.msreport.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/** DTO para actualizar únicamente el estado de un reporte. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportUpdateStatusDTO {

    @NotNull(message = "El id del nuevo estado es obligatorio")
    private Long idStatus;
}
