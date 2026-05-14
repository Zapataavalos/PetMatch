package com.petmatch.msreporttype.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportTypeDTO {
    @NotBlank(message = "El nombre del tipo de reporte es obligatorio")
    private String name;
}
