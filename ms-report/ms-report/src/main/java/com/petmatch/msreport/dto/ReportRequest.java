package com.petmatch.msreport.dto;

import com.petmatch.msreport.model.ReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "La descripcion es obligatoria")
        @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
        String descripcion,

        @NotBlank(message = "La ubicacion es obligatoria")
        @Size(max = 180, message = "La ubicacion no puede superar los 180 caracteres")
        String ubicacion,

        @NotNull(message = "El estado es obligatorio")
        ReportStatus estado,

        String imagenUrl,
        Double latitud,
        Double longitud
) {
}
